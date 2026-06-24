<?php

declare(strict_types=1);

namespace Sdkwork\Utils\Tests;

use PHPUnit\Framework\TestCase;
use Sdkwork\Utils\CryptoUtils;
use Sdkwork\Utils\I18nUtils;
use Sdkwork\Utils\OptionalUtils;
use Sdkwork\Utils\StringUtils;

final class UtilsTest extends TestCase
{
    public function testStringHelpers(): void
    {
        $this->assertTrue(StringUtils::isBlank('  '));
        $this->assertSame('helloWorld', StringUtils::camelCase('hello_world'));
        $this->assertSame('hello-sdk-work', StringUtils::slugify('Hello, SDKWork!'));
    }

    public function testCryptoHelpers(): void
    {
        $this->assertSame(
            '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824',
            CryptoUtils::sha256Hash('hello')
        );
        $this->assertSame(
            'b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4',
            CryptoUtils::hmacSha256('payload', 'secret')
        );
    }

    public function testOptionalAndI18nHelpers(): void
    {
        $this->assertSame('ok', OptionalUtils::coalesce(null, '', '  ', 'ok'));
        $this->assertSame('fallback', OptionalUtils::defaultIfBlank('  ', 'fallback'));
        $this->assertSame('1,234.50', I18nUtils::formatNumberLocale(1234.5, 'en-US', 2));
        $this->assertSame('1.234,50', I18nUtils::formatNumberLocale(1234.5, 'de-DE', 2));
    }
}
