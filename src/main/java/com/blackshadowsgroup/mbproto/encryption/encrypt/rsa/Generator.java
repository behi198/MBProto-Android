package com.blackshadowsgroup.mbproto.encryption.encrypt.rsa;
import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

/**
 * @author Behi198
 */
public class Generator {
    private static int _type;

    private static BigInteger _m = (new BigInteger("2")).pow(32);
    private static BigInteger _a = new BigInteger("69069");
    private static BigInteger _b = new BigInteger("0");
    private static BigInteger _rn = BigInteger.ONE;

    private static Random _rnd = new Random();

    public static void Initialize(int t) {
        _type = t;

        if (_type == 0) {
            _m = (new BigInteger("2")).pow(32);
            _a = new BigInteger("69069");
            _b = new BigInteger("0");
            _rn = BigInteger.ONE;
        } else if (_type == 1) {
            _rnd = new Random();
        } else {
            _rnd = new Random();
        }
    }

    public static void SetLcg(BigInteger mIn, BigInteger aIn, BigInteger bIn, BigInteger rnIn) {
        _type = 0;

        _m = mIn;
        _a = aIn;
        _b = bIn;
        _rn = rnIn;
    }

    public static BigInteger Random(BigInteger a, BigInteger b) {
        BigInteger retValue;

        if (_type == 0) {
            retValue = a.add(Lcg().mod(b.subtract(a).add(BigInteger.ONE)));
        } else if (_type == 1) {
            BigInteger count = b.subtract(a);

            BigInteger digits = new BigInteger("0");
            BigInteger ten = new BigInteger("10");
            while ((count.divide(ten)).intValue() > 0) {
                count = count.divide(ten);

                digits = digits.add(BigInteger.ONE);
            }

            String entropy = Entropy();

            String retVal = "";

            while (retVal.length() < digits.toString().length()) {
                retVal = retVal + entropy.charAt(_rnd.nextInt(entropy.length()));
            }

            // We get the number, but we might be too high, so we do a mod
            retValue = new BigInteger(retVal);

            retValue = a.add(retValue.mod(b));
        } else {
            BigInteger count = b.subtract(a);

            BigInteger digits = new BigInteger("0");
            BigInteger ten = new BigInteger("10");
            while ((count.mod(ten)).compareTo(BigInteger.ZERO) > 0) {
                count = count.mod(ten);

                digits.add(BigInteger.ONE);
            }

            String retVal = String.valueOf((_rnd.nextInt(2100000000) + 1000000000));
            BigInteger temp = new BigInteger(retVal.length() + "");
            while (temp.compareTo(digits) < 0) {
                retVal = retVal + _rnd.nextInt(1000);
            }

            // We get the number, but we might be too high, so we do a mod
            retValue = new BigInteger(retVal);

            retValue = a.add(retValue.mod(b));
        }

        return retValue;
    }

    private static String Entropy() {
        Date date = new Date();
        String entropy = String.valueOf(date.getTime());

//        PerformanceCounter cpuCounter = new PerformanceCounter
//        {
//            CategoryName = "Processor",
//                    CounterName = "% Processor Time",
//                    InstanceName = "_Total"
//        } ;

//        entropy = entropy + cpuCounter.NextValue();

        return entropy;
    }

    private static BigInteger Lcg() {
        _rn = (_a.multiply(_rn).add(_b)).mod(_m);
        return _rn;
    }
}