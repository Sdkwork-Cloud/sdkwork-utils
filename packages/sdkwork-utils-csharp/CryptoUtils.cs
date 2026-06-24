using System.Security.Cryptography;
using System.Text;

namespace Sdkwork.Utils;

public static class CryptoUtils
{
    public static string Sha256Hash(string value) =>
        Convert.ToHexString(SHA256.HashData(Encoding.UTF8.GetBytes(value))).ToLowerInvariant();

    public static string Sha256Hash(byte[] value) =>
        Convert.ToHexString(SHA256.HashData(value)).ToLowerInvariant();

    public static string HmacSha256(string value, string secret) =>
        Convert.ToHexString(HMACSHA256.HashData(Encoding.UTF8.GetBytes(secret), Encoding.UTF8.GetBytes(value)))
            .ToLowerInvariant();

    public static bool SecureCompare(string left, string right)
    {
        if (left.Length != right.Length) return false;
        var result = 0;
        for (var index = 0; index < left.Length; index++)
        {
            result |= left[index] ^ right[index];
        }
        return result == 0;
    }
}
