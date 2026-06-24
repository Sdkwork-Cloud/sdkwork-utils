namespace Sdkwork.Utils;

public static class IdUtils
{
    private const string Alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static string Uuid() => Guid.NewGuid().ToString();

    public static string RandomString(int length)
    {
        var random = Random.Shared;
        return new string(Enumerable.Range(0, length).Select(_ => Alphanumeric[random.Next(Alphanumeric.Length)]).ToArray());
    }
}
