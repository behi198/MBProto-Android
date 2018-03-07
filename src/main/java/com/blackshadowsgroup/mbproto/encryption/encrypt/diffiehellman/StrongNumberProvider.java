package com.blackshadowsgroup.mbproto.encryption.encrypt.diffiehellman;

import java.security.SecureRandom;

public class StrongNumberProvider
    {
        private static SecureRandom csp = new SecureRandom();

        public int NextUInt32()
        {
            byte[] res = new byte[4];
            csp.nextBytes(res);
            return BitConverter.toInt32(res, 0);
        }

        public int NextInt()
        {
            byte[] res = new byte[4];
            csp.nextBytes(res);
            return BitConverter.toInt32(res, 0);
        }

        public float NextSingle()
        {
            float numerator = NextUInt32();
            float denominator = Float.MAX_VALUE;
            return numerator / denominator;
        }
    }

