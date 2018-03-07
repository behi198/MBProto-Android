package com.blackshadowsgroup.mbproto.encryption.encrypt.rsa;

import java.math.BigInteger;

public class PrimeNumber {
    private BigInteger _iterationTested = new BigInteger("0");
    private BigInteger _numberTested = new BigInteger("0");
    private BigInteger _number = new BigInteger("2");

    private BigInteger _s = new BigInteger("10");

    private boolean _found;

    private boolean _running;

    public BigInteger GetPrimeNumber() {
        return _number;
    }

    public BigInteger GetTestedCount() {
        return _numberTested;
    }

    public BigInteger GetTestedIterations() {
        return _iterationTested;
    }

    public boolean GetFoundPrime() {
        return _found;
    }

    public void StopEngine() {
        _running = false;
    }

    public boolean odd(BigInteger val) {
        return !val.mod(new BigInteger("2")).equals(BigInteger.ZERO);
    }

    public void TestRabinMiller() {
        _found = false;
        _running = true;

        if (!odd(_number)) {
            _found = true;
        }

        if (odd(_number)) {
            _found = RabinMillerTest(_number, _s);
        }

        _running = false;
    }

    public void TestNaive() {
        _found = false;
        _running = true;

        if (BigInteger.valueOf(2).compareTo(_number) == 0) {
            _found = true;
        }

        if (odd(_number)) {
            _found = NaiveTest(_number);
        }

        _running = false;
    }

    private boolean NaiveTest(BigInteger r) {
        if (BigInteger.valueOf(3).compareTo(r) > 0) {
            return true;
        }

        _iterationTested = new BigInteger("0");

        BigInteger g = Sqrt(r);

        BigInteger j = new BigInteger("3");

        while (j.compareTo(g) <= 0) {
            _iterationTested = _iterationTested.add(BigInteger.ONE);

            if (_number.mod(j).intValue() == 0) {
                break;
            }

            j = j.add(BigInteger.ONE);
            j = j.add(BigInteger.ONE);
        }

        if (j.compareTo(g) > 0) {
            return true;
        }
        return false;
    }

    private boolean RabinMillerTest(BigInteger r, BigInteger s) {
        _iterationTested = new BigInteger("0");

        //
        // Find D and K so equality is correct: d*2^k = r - 1
        //

        BigInteger d = r.subtract(BigInteger.ONE);
        BigInteger k = new BigInteger("0");

        while (!odd(d)) {
            d = d.divide(new BigInteger("2"));
            k = k.add(BigInteger.ONE);
        }

        for (BigInteger j = BigInteger.ONE; j.compareTo(s) <= 0; j = j.add(BigInteger.ONE)) {
            _iterationTested.add(BigInteger.ONE);

            BigInteger a = Generator.Random(new BigInteger("2"), (r.subtract(BigInteger.ONE)));
            BigInteger x = a.modPow(d, r);

            if (x.compareTo(BigInteger.ONE) != 0) {
                for (BigInteger i = new BigInteger("0"); i.compareTo(k.subtract(BigInteger.ONE)) < 0; i = i.add(BigInteger.ONE)) {
                    if (x.compareTo(_number.subtract(BigInteger.ONE)) == 0) {
                        break;
                    }

                    x = x.modPow(new BigInteger("2"), _number);
                }

                if (x.compareTo(_number.subtract(BigInteger.ONE)) != 0) {
                    return false;
                }
            }

            if (_running == false) {
                return false;
            }
        }

        return true;
    }

    private BigInteger Sqrt(BigInteger n) {
        if (n.compareTo(new BigInteger("0")) == 0) {
            return new BigInteger("0");
        }

        if (n.compareTo(new BigInteger("0")) > 0) {
            int bitLength = Integer.parseInt(String.valueOf(Math.ceil(Math.log(n.doubleValue())/ Math.log(2))));
            BigInteger root = BigInteger.ONE.shiftLeft(bitLength / 2);

            while (!IsSqrt(n, root)) {
                root = root.add(n.divide(root));
                root = root.divide(new BigInteger("2"));
            }

            return root;
        }

        throw new ArithmeticException("NaN");
    }

    private boolean IsSqrt(BigInteger n, BigInteger root) {
        BigInteger lowerBound = root.pow(2);
        BigInteger upperBound = root.add(BigInteger.ONE).pow(2);

        return ((n.compareTo(lowerBound) >= 0) && (n.compareTo(upperBound) < 0));
    }


    public void SetNumber(BigInteger num) {
        _number = num;
    }

    public void SetRabinMiller(BigInteger sNew) {
        BigInteger two = new BigInteger("2");
        if (sNew.compareTo(two) < 0) {
            sNew = two;
        }

        _s = sNew;
    }

    public void RabinMiller() {
        _running = true;
        _found = false;

        // No negative primes
        if (_number.compareTo(BigInteger.ONE)<0) {
            _number = BigInteger.ONE;
        }

        BigInteger two = new BigInteger("2");
        // Two is prime
        if (_number.compareTo(two) <= 0) {
            return;
        }

        // Other even numbers arent primes
        if (!odd(_number)) {
            _number = _number.add(BigInteger.ONE);
        }

        // First five is prime
        if (_number.compareTo(new BigInteger("5")) == 0) {
            _running = false;
            _found = true;

            return;
        }

        _numberTested = new BigInteger("0");

        while (_running) {
            if (RabinMillerTest(_number, _s)) {
                _found = true;
                _running = false;

                return;
            }

            // Skip number 5
            if (_number.mod(new BigInteger("10")).compareTo(new BigInteger("3")) == 0) {
                _number = _number.add(new BigInteger("4"));
            } else {
                _number = _number.add(new BigInteger("2"));
            }

            _numberTested = _numberTested.add(BigInteger.ONE);
        }
    }

    public void Naive() {
        _running = true;
        _found = false;

        _numberTested = new BigInteger("0");

        // No negative primes
        if (_number.compareTo(BigInteger.ONE) < 0) {
            _number = BigInteger.ONE;
        }

        // Two is prime
        if (_number.compareTo(new BigInteger("2")) <= 0) {
            return;
        }

        // Other even numbers arent primes
        if (!odd(_number)) {
            _number = _number.add(BigInteger.ONE);
        }

        // First five is prime
        if (_number.compareTo(new BigInteger("5")) == 0) {
            _running = false;
            _found = true;

            return;
        }

        while (_running) {
            if (NaiveTest(_number)) {
                _found = true;
                _running = false;

                return;
            }

            // Skip number 5
            if (_number.mod(new BigInteger("10")).compareTo(new BigInteger("3")) == 0) {
                _number = _number.add(new BigInteger("4"));
            } else {
                _number = _number.add(new BigInteger("2"));
            }

            _numberTested = _numberTested.add(BigInteger.ONE);
        }
    }
}
