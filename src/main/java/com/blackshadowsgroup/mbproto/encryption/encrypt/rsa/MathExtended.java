package com.blackshadowsgroup.mbproto.encryption.encrypt.rsa;

import java.math.BigInteger;

public class MathExtended
    {
        public static BigInteger ModularLinearEquationSolver (BigInteger a, BigInteger b, BigInteger n)
        {
            BigInteger x = null;
            BigInteger y = null;
            BigInteger d = null;

            ExtendedEuclid(a, n);

            if (b.mod(d).intValue() ==0)
            {
                x = (x.multiply(b.divide(d))).mod(n);

                if (x.intValue() < 0)
                {
                    return (x.add(n)).mod(n);
                }
                return x;
            }
            return new BigInteger("-1");
        }

        static Result ExtendedEuclid (BigInteger a, BigInteger b)
        {
            BigInteger x = BigInteger.ZERO;
            BigInteger y = BigInteger.ONE;
            Result result = new Result();
            result.x = BigInteger.ONE;
            result.y = BigInteger.ZERO;

            while (b.intValue() != 0)
            {
                BigInteger quotient = a.divide(b);
                BigInteger temp = b;

                b = a.mod(b);
                a = temp;

                temp = x;
                x = result.x.subtract(quotient.multiply(x));
                result.x = temp;

                temp = y;
                y = result.y.subtract(quotient.multiply(y));
                result.y = temp;
            }

            result.d = a;
            return result;
        }
    }

class Result{
    BigInteger d;
    BigInteger x;
    BigInteger y;
}
