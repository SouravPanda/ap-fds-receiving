package com.walmart.finance.ap.fds.receiving.mesh;

import java.io.ObjectStreamException;
import java.security.KeyRep;

public class ServiceKeyRep extends KeyRep	{
    private static final long serialVersionUID = -7213340660431987616L;

    public ServiceKeyRep(KeyRep.Type type, String algorithm, String format, byte[] encoded) {
        super(type, algorithm, format, encoded);
    }

    protected Object readResolve() throws ObjectStreamException {
        return super.readResolve();
    }
}