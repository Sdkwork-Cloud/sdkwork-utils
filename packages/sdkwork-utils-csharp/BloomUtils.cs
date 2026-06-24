using System.Security.Cryptography;

namespace Sdkwork.Utils;

public sealed class BloomFilter
{
    public BloomFilter(int bitCount, int hashCount, byte[] bits)
    {
        BitCount = bitCount;
        HashCount = hashCount;
        Bits = bits;
    }

    public int BitCount { get; }
    public int HashCount { get; }
    public byte[] Bits { get; }
}

public static class BloomUtils
{
    private const int MinBitCount = 64;
    private static readonly double Ln2 = Math.Log(2);

    public static int EstimateBitCount(int expectedItems, double falsePositiveRate)
    {
        if (expectedItems <= 0)
        {
            return MinBitCount;
        }

        var rate = Math.Min(0.25, Math.Max(0.0001, falsePositiveRate));
        var size = (-expectedItems * Math.Log(rate)) / (Ln2 * Ln2);
        return Math.Max(MinBitCount, (int)Math.Ceiling(size));
    }

    public static int EstimateHashCount(int expectedItems, int bitCount)
    {
        if (expectedItems <= 0)
        {
            return 1;
        }

        var count = ((double)bitCount / expectedItems) * Ln2;
        return Math.Max(1, (int)Math.Round(count));
    }

    public static BloomFilter Create(int expectedItems, double falsePositiveRate)
    {
        var bitCount = EstimateBitCount(expectedItems, falsePositiveRate);
        var hashCount = EstimateHashCount(expectedItems, bitCount);
        return new BloomFilter(bitCount, hashCount, new byte[(bitCount + 7) / 8]);
    }

    public static void Add(BloomFilter filter, string value)
    {
        foreach (var index in HashPositions(System.Text.Encoding.UTF8.GetBytes(value), filter.BitCount, filter.HashCount))
        {
            var byteIndex = index / 8;
            var bitIndex = index % 8;
            filter.Bits[byteIndex] |= (byte)(1 << bitIndex);
        }
    }

    public static bool MightContain(BloomFilter filter, string value)
    {
        foreach (var index in HashPositions(System.Text.Encoding.UTF8.GetBytes(value), filter.BitCount, filter.HashCount))
        {
            var byteIndex = index / 8;
            var bitIndex = index % 8;
            if ((filter.Bits[byteIndex] & (1 << bitIndex)) == 0)
            {
                return false;
            }
        }

        return true;
    }

    private static IEnumerable<int> HashPositions(byte[] value, int bitCount, int hashCount)
    {
        var digest = SHA256.HashData(value);
        var h1 = ReadU32(digest, 0);
        var h2 = ReadU32(digest, 4);
        if (h2 == 0)
        {
            h2 = 1;
        }

        var modulus = (ulong)bitCount;
        for (var index = 0; index < hashCount; index++)
        {
            yield return (int)((h1 + (ulong)index * h2) % modulus);
        }
    }

    private static ulong ReadU32(byte[] digest, int offset)
    {
        return ((ulong)digest[offset] << 24)
            | ((ulong)digest[offset + 1] << 16)
            | ((ulong)digest[offset + 2] << 8)
            | digest[offset + 3];
    }
}
