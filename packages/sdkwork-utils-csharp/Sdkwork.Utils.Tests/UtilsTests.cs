using Sdkwork.Utils;
using Xunit;

namespace Sdkwork.Utils.Tests;

public class UtilsTests
{
    [Fact]
    public void StringHelpers()
    {
        Assert.True(StringUtils.IsBlank("  "));
        Assert.Equal("helloWorld", StringUtils.CamelCase("hello_world"));
        Assert.Equal("hello-sdk-work", StringUtils.Slugify("Hello, SDKWork!"));
    }

    [Fact]
    public void CryptoHelpers()
    {
        Assert.Equal(
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
            CryptoUtils.Sha256Hash("hello"));
        Assert.Equal(
            "b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4",
            CryptoUtils.HmacSha256("payload", "secret"));
    }

    [Fact]
    public void OptionalResultAndI18nHelpers()
    {
        Assert.Equal("ok", OptionalUtils.Coalesce(null, "", "  ", "ok"));
        Assert.Equal("fallback", OptionalUtils.DefaultIfBlank("  ", "fallback"));
        Assert.Equal(42, ResultUtils.UnwrapOr(ResultValue<int>.Success(42), 0));
        Assert.Equal("1,234.50", I18nUtils.FormatNumberLocale(1234.5, "en-US", 2));
        Assert.Equal("1.234,50", I18nUtils.FormatNumberLocale(1234.5, "de-DE", 2));
    }
}
