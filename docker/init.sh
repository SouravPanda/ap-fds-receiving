#!/bin/sh

appKey="${SR_APP_KEY}"
appEnv="${SR_APP_ENV}"
appAddr="${SR_APP_ADDR}"
echo "appKey:$appKey"
echo "appEnv:$appEnv"
echo "appAddr:$appAddr"

appAddr=$(echo $appAddr | sed 's/\//\\\//g')

echo "s/SR_APP_ADDR/$appAddr/"

sed -i "s/SR_APP_KEY/$appKey/" /config.yaml
sed -i "s/SR_APP_ENV/$appEnv/" /config.yaml
sed -i "s/SR_APP_ADDR/$appAddr/" /config.yaml

cat /config.yaml

echo "Starting ssh service"
rc-status
service start sshd
touch /run/openrc/softlevel
service sshd start
echo "Started ssh service"

jars="/linkerd/soa-linkerd.jar"
if [ -n "$L5D_HOME" ] && [ -d $L5D_HOME/plugins ]; then
  for jar in $L5D_HOME/plugins/*.jar ; do
    jars="$jars:$jar"
  done
fi
jars="$jars:/linkerd-scm-config/"

if [ -z "$STRATI_OPTIONS" ]; then
  STRATI_OPTIONS="-Druntime.context.appName=${STRATI_APP_NAME}                   \
  -Druntime.context.appVersion=${STRATI_APP_VERSION}                             \
  -Druntime.context.computeID=${STRATI_COMPUTEID}                                \
  -Druntime.context.computeName=${STRATI_COMPUTENAME}                            \
  -Druntime.context.hostname=${STRATI_HOSTNAME}                                  \
  -Druntime.context.cloud=${STRATI_CLOUD}                                        \
  -Dscm.server.access.enabled=${STRATI_ENABLE_CCM}                               \
  -Druntime.context.system.property.override.enabled=${STRATI_OVERRIDE_PROPERTY} \
  -Druntime.context.environmentType=${STRATI_ENVIRONMENT_TYPE}                   \
  -Druntime.context.environment=${STRATI_ENVIRONMENT}                            \
  "
fi

DEFAULT_JVM_OPTIONS="-Djava.net.preferIPv4Stack=true             \
   -Dsun.net.inetaddr.ttl=60                                     \
   -XX:+UnlockExperimentalVMOptions                              \
   -XX:+UseCGroupMemoryLimitForHeap                              \
   -XX:+AggressiveOpts                                           \
   -XX:+UseConcMarkSweepGC                                       \
   -XX:+CMSParallelRemarkEnabled                                 \
   -XX:+CMSClassUnloadingEnabled                                 \
   -XX:+ScavengeBeforeFullGC                                     \
   -XX:+CMSScavengeBeforeRemark                                  \
   -XX:+UseCMSInitiatingOccupancyOnly                            \
   -XX:CMSInitiatingOccupancyFraction=70                         \
   -XX:-TieredCompilation                                        \
   -XX:+UseStringDeduplication                                   \
   -XX:+AlwaysPreTouch                                           \
   -Dcom.twitter.util.events.sinkEnabled=false                   \
   -Dorg.apache.thrift.readLength=10485760                       \
   -Djdk.nio.maxCachedBufferSize=262144                          \
   -Dio.netty.threadLocalDirectBufferSize=0                      \
   -Dio.netty.recycler.maxCapacity=4096                          \
   -Dio.netty.allocator.numHeapArenas=${FINAGLE_WORKERS:-8}      \
   -Dio.netty.allocator.numDirectArenas=${FINAGLE_WORKERS:-8}    \
   -Dcom.twitter.finagle.netty4.numWorkers=${FINAGLE_WORKERS:-8} \
   ${STRATI_OPTIONS}                                             \
   ${LOCAL_JVM_OPTIONS:-}                                        \
   ${DEBUG_OPTIONS:-}                                            \
   "
if [ -n "$JAVA_HOME" ]; then
  JAVA="$JAVA_HOME/bin/java"
elif [ -x "/usr/bin/java" ]; then
  JAVA="/usr/bin/java"
elif [ -n "$(which java)" ]; then
  JAVA=$(which java)
else
  echo "ERROR: Could not find Java!"
  exit 1
fi

set -x

mkdir -p /log/mesh

exec "${JAVA}" -XX:+PrintCommandLineFlags \
     ${JVM_OPTIONS:-$DEFAULT_JVM_OPTIONS} -cp $jars -server \
     io.buoyant.linkerd.Main "$@" 1>>/log/mesh/mesh.log 2>&1 &

exec java -jar app.jar -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} && fg