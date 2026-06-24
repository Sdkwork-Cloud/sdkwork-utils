<?php

declare(strict_types=1);

namespace Sdkwork\Utils\Tests;

use PHPUnit\Framework\TestCase;
use Sdkwork\Utils\BytesUtils;
use Sdkwork\Utils\BloomUtils;
use Sdkwork\Utils\BooleanUtils;
use Sdkwork\Utils\CollectionUtils;
use Sdkwork\Utils\CompareUtils;
use Sdkwork\Utils\CryptoUtils;
use Sdkwork\Utils\CurrencyUtils;
use Sdkwork\Utils\DateTimeUtils;
use Sdkwork\Utils\EncodingUtils;
use Sdkwork\Utils\I18nUtils;
use Sdkwork\Utils\IdUtils;
use Sdkwork\Utils\NumberUtils;
use Sdkwork\Utils\ObjectUtils;
use Sdkwork\Utils\OptionalUtils;
use Sdkwork\Utils\PathUtils;
use Sdkwork\Utils\ResultValue;
use Sdkwork\Utils\StringUtils;
use Sdkwork\Utils\ValidationUtils;

final class ConformanceTest extends TestCase
{
    private array $fixtures;

    protected function setUp(): void
    {
        $path = dirname(__DIR__, 3) . '/specs/conformance/fixtures.json';
        $this->fixtures = json_decode((string) file_get_contents($path), true, flags: JSON_THROW_ON_ERROR);
    }

    public function testConformanceFixtures(): void
    {
        foreach ($this->fixtures['string']['is_blank'] as $item) {
            $this->assertSame($item['output'], StringUtils::isBlank($item['input']));
        }
        foreach ($this->fixtures['string']['trim'] as $item) {
            $this->assertSame($item['output'], StringUtils::trim($item['input']));
        }
        foreach ($this->fixtures['string']['truncate'] as $item) {
            $this->assertSame($item['output'], StringUtils::truncate($item['input'], $item['max_len'], $item['suffix']));
        }
        foreach ($this->fixtures['string']['capitalize'] as $item) {
            $this->assertSame($item['output'], StringUtils::capitalize($item['input']));
        }
        foreach ($this->fixtures['string']['camel_case'] as $item) {
            $this->assertSame($item['output'], StringUtils::camelCase($item['input']));
        }
        foreach ($this->fixtures['string']['snake_case'] as $item) {
            $this->assertSame($item['output'], StringUtils::snakeCase($item['input']));
        }
        foreach ($this->fixtures['string']['kebab_case'] as $item) {
            $this->assertSame($item['output'], StringUtils::kebabCase($item['input']));
        }
        foreach ($this->fixtures['string']['slugify'] as $item) {
            $this->assertSame($item['output'], StringUtils::slugify($item['input']));
        }
        foreach ($this->fixtures['string']['mask'] as $item) {
            $this->assertSame($item['output'], StringUtils::mask($item['input'], $item['visible_start'], $item['visible_end']));
        }
        foreach ($this->fixtures['string']['pad_start'] as $item) {
            $this->assertSame($item['output'], StringUtils::padStart($item['input'], $item['target_len']));
        }
        foreach ($this->fixtures['string']['pad_end'] as $item) {
            $this->assertSame($item['output'], StringUtils::padEnd($item['input'], $item['target_len']));
        }
        foreach ($this->fixtures['string']['starts_with'] as $item) {
            $this->assertSame($item['output'], StringUtils::startsWith($item['input'], $item['prefix']));
        }
        foreach ($this->fixtures['string']['ends_with'] as $item) {
            $this->assertSame($item['output'], StringUtils::endsWith($item['input'], $item['suffix']));
        }
        foreach ($this->fixtures['string']['contains'] as $item) {
            $this->assertSame($item['output'], StringUtils::contains($item['input'], $item['substring']));
        }
        foreach ($this->fixtures['string']['replace_all'] as $item) {
            $this->assertSame($item['output'], StringUtils::replaceAll($item['input'], $item['search'], $item['replacement']));
        }
        foreach ($this->fixtures['string']['split'] as $item) {
            $this->assertSame($item['output'], StringUtils::split($item['input'], $item['delimiter'], $item['trim_parts']));
        }
        foreach ($this->fixtures['string']['join'] as $item) {
            $this->assertSame($item['output'], StringUtils::join($item['parts'], $item['separator']));
        }
        foreach ($this->fixtures['string']['repeat'] as $item) {
            $this->assertSame($item['output'], StringUtils::repeat($item['input'], $item['count']));
        }
        foreach ($this->fixtures['string']['normalize_whitespace'] as $item) {
            $this->assertSame($item['output'], StringUtils::normalizeWhitespace($item['input']));
        }
        foreach ($this->fixtures['string']['template'] as $item) {
            $this->assertSame($item['output'], StringUtils::template($item['template'], $item['values']));
        }

        foreach ($this->fixtures['datetime']['now'] as $item) {
            $this->assertSame($item['valid'], DateTimeUtils::now() instanceof \DateTimeImmutable);
        }
        foreach ($this->fixtures['datetime']['format'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime($parsed));
        }
        foreach ($this->fixtures['datetime']['parse'] as $item) {
            $this->assertSame($item['valid'], DateTimeUtils::parseDatetime($item['input']) !== null);
        }
        $diff = $this->fixtures['datetime']['diff_millis'];
        $earlier = DateTimeUtils::parseDatetime($diff['earlier']);
        $later = DateTimeUtils::parseDatetime($diff['later']);
        $this->assertNotNull($earlier);
        $this->assertNotNull($later);
        $this->assertSame($diff['output'], DateTimeUtils::diffMillis($earlier, $later));
        foreach ($this->fixtures['datetime']['add_days'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime(DateTimeUtils::addDays($parsed, $item['days'])));
        }
        foreach ($this->fixtures['datetime']['add_hours'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime(DateTimeUtils::addHours($parsed, $item['hours'])));
        }
        foreach ($this->fixtures['datetime']['add_minutes'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime(DateTimeUtils::addMinutes($parsed, $item['minutes'])));
        }
        foreach ($this->fixtures['datetime']['is_before'] as $item) {
            $left = DateTimeUtils::parseDatetime($item['left']);
            $right = DateTimeUtils::parseDatetime($item['right']);
            $this->assertNotNull($left);
            $this->assertNotNull($right);
            $this->assertSame($item['output'], DateTimeUtils::isBefore($left, $right));
        }
        foreach ($this->fixtures['datetime']['is_after'] as $item) {
            $left = DateTimeUtils::parseDatetime($item['left']);
            $right = DateTimeUtils::parseDatetime($item['right']);
            $this->assertNotNull($left);
            $this->assertNotNull($right);
            $this->assertSame($item['output'], DateTimeUtils::isAfter($left, $right));
        }
        foreach ($this->fixtures['datetime']['start_of_day_utc'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime(DateTimeUtils::startOfDayUtc($parsed)));
        }
        foreach ($this->fixtures['datetime']['end_of_day_utc'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime(DateTimeUtils::endOfDayUtc($parsed)));
        }
        foreach ($this->fixtures['datetime']['to_unix_millis'] as $item) {
            $parsed = DateTimeUtils::parseDatetime($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::toUnixMillis($parsed));
        }
        foreach ($this->fixtures['datetime']['from_unix_millis'] as $item) {
            $parsed = DateTimeUtils::fromUnixMillis($item['input']);
            $this->assertNotNull($parsed);
            $this->assertSame($item['output'], DateTimeUtils::formatDatetime($parsed));
        }
        foreach ($this->fixtures['datetime']['is_same_instant'] as $item) {
            $left = DateTimeUtils::parseDatetime($item['left']);
            $right = DateTimeUtils::parseDatetime($item['right']);
            $this->assertNotNull($left);
            $this->assertNotNull($right);
            $this->assertSame($item['output'], DateTimeUtils::isSameInstant($left, $right));
        }

        foreach ($this->fixtures['encoding']['base64_encode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::base64Encode($item['input']));
        }
        foreach ($this->fixtures['encoding']['base64_decode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::base64Decode($item['input']));
        }
        foreach ($this->fixtures['encoding']['hex_encode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::hexEncode($item['input']));
        }
        foreach ($this->fixtures['encoding']['hex_decode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::hexDecode($item['input']));
        }
        foreach ($this->fixtures['encoding']['url_encode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::urlEncode($item['input']));
        }
        foreach ($this->fixtures['encoding']['url_decode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::urlDecode($item['input']));
        }
        foreach ($this->fixtures['encoding']['base64url_encode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::base64urlEncode($item['input']));
        }
        foreach ($this->fixtures['encoding']['base64url_decode'] as $item) {
            $this->assertSame($item['output'], EncodingUtils::base64urlDecode($item['input']));
        }

        $merge = $this->fixtures['object']['deep_merge'];
        $this->assertSame($merge['output'], ObjectUtils::deepMerge($merge['base'], $merge['overlay']));
        $shallow = $this->fixtures['object']['shallow_merge'];
        $this->assertSame($shallow['output'], ObjectUtils::shallowMerge($shallow['base'], $shallow['overlay']));
        $pathCase = $this->fixtures['object']['set_get_path'];
        $target = [];
        ObjectUtils::setPath($target, $pathCase['path'], $pathCase['value']);
        $this->assertSame($pathCase['output'], ObjectUtils::getPath($target, $pathCase['path']));
        foreach ($this->fixtures['object']['get_path'] as $item) {
            $this->assertSame($item['output'], ObjectUtils::getPath($item['source'], $item['path']));
        }
        foreach ($this->fixtures['object']['set_path'] as $item) {
            $target = [];
            ObjectUtils::setPath($target, $item['path'], $item['value']);
            $this->assertSame($item['read_back'], ObjectUtils::getPath($target, $item['path']));
        }
        foreach ($this->fixtures['object']['pick'] as $item) {
            $this->assertSame($item['output'], ObjectUtils::pick($item['source'], $item['keys']));
        }
        foreach ($this->fixtures['object']['omit'] as $item) {
            $this->assertSame($item['output'], ObjectUtils::omit($item['source'], $item['keys']));
        }
        foreach ($this->fixtures['object']['compact'] as $item) {
            $this->assertSame($item['output'], ObjectUtils::compact($item['input']));
        }
        $hasPathBase = ['user' => ['name' => 'Ada']];
        foreach ($this->fixtures['object']['has_path'] as $item) {
            $this->assertSame($item['exists'], ObjectUtils::hasPath($hasPathBase, $item['path']));
        }
        foreach ($this->fixtures['object']['keys'] as $item) {
            $this->assertSame($item['output'], ObjectUtils::keys($item['input']));
        }
        foreach ($this->fixtures['object']['values'] as $item) {
            $this->assertSame($item['output'], ObjectUtils::values($item['input']));
        }

        foreach ($this->fixtures['crypto']['sha256_hash'] as $item) {
            $this->assertSame($item['output'], CryptoUtils::sha256Hash($item['input']));
        }
        foreach ($this->fixtures['crypto']['hmac_sha256'] as $item) {
            $this->assertSame($item['output'], CryptoUtils::hmacSha256($item['input'], $item['secret']));
        }
        foreach ($this->fixtures['crypto']['secure_compare'] as $item) {
            $this->assertSame($item['output'], CryptoUtils::secureCompare($item['left'], $item['right']));
        }

        foreach ($this->fixtures['number']['clamp'] as $item) {
            $this->assertSame($item['output'], NumberUtils::clamp($item['value'], $item['min'], $item['max']));
        }
        foreach ($this->fixtures['number']['round'] as $item) {
            $this->assertSame($item['output'], NumberUtils::round($item['value'], $item['decimals']));
        }
        foreach ($this->fixtures['number']['format_number'] as $item) {
            $this->assertSame($item['output'], NumberUtils::formatNumber($item['value'], $item['decimals']));
        }
        foreach ($this->fixtures['number']['parse_number'] as $item) {
            $this->assertSame($item['output'], NumberUtils::parseNumber($item['input']));
        }
        foreach ($this->fixtures['number']['is_integer'] as $item) {
            $this->assertSame($item['output'], NumberUtils::isInteger($item['value']));
        }
        foreach ($this->fixtures['number']['parse_int'] as $item) {
            $this->assertSame($item['output'], NumberUtils::parseInt($item['input']));
        }
        foreach ($this->fixtures['number']['percent_format'] as $item) {
            $this->assertSame($item['output'], NumberUtils::percentFormat($item['value'], $item['decimals']));
        }
        foreach ($this->fixtures['number']['in_range'] as $item) {
            $this->assertSame($item['output'], NumberUtils::inRange($item['value'], $item['min'], $item['max']));
        }
        foreach ($this->fixtures['number']['abs'] as $item) {
            $this->assertSame($item['output'], NumberUtils::abs($item['input']));
        }

        foreach ($this->fixtures['collection']['unique'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::unique($item['input']));
        }
        foreach ($this->fixtures['collection']['chunk'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::chunk($item['input'], $item['size']));
        }
        foreach ($this->fixtures['collection']['flatten'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::flatten($item['input']));
        }
        foreach ($this->fixtures['collection']['compact'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::compact($item['input']));
        }
        foreach ($this->fixtures['collection']['group_by'] as $item) {
            $grouped = CollectionUtils::groupBy($item['input'], static fn (array $entry): string => $entry['type']);
            $this->assertSame($item['output'], $grouped);
        }
        foreach ($this->fixtures['collection']['first'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::first($item['input']));
        }
        foreach ($this->fixtures['collection']['last'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::last($item['input']));
        }
        foreach ($this->fixtures['collection']['sort_by'] as $item) {
            $sorted = CollectionUtils::sortBy($item['input'], static fn (array $entry): string => $entry['k']);
            $this->assertSame($item['output'], $sorted);
        }
        foreach ($this->fixtures['collection']['key_by'] as $item) {
            $keyed = CollectionUtils::keyBy($item['input'], static fn (array $entry): string => $entry['id']);
            $this->assertSame($item['output'], $keyed);
        }
        foreach ($this->fixtures['collection']['filter'] as $item) {
            $filtered = CollectionUtils::filter($item['input'], static fn (int $value): bool => $value > $item['threshold']);
            $this->assertSame($item['output'], $filtered);
        }
        foreach ($this->fixtures['collection']['find'] as $item) {
            $this->assertSame($item['output'], CollectionUtils::find($item['input'], static fn (int $value): bool => $value > $item['threshold']));
        }

        foreach ($this->fixtures['validation']['is_email'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isEmail($item['input']));
        }
        foreach ($this->fixtures['validation']['is_uuid'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isUuid($item['input']));
        }
        foreach ($this->fixtures['validation']['is_url'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isUrl($item['input']));
        }
        foreach ($this->fixtures['validation']['is_numeric'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isNumeric($item['input']));
        }
        foreach ($this->fixtures['validation']['is_ipv4'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isIpv4($item['input']));
        }
        foreach ($this->fixtures['validation']['is_ipv6'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isIpv6($item['input']));
        }
        foreach ($this->fixtures['validation']['is_phone_e164'] as $item) {
            $this->assertSame($item['output'], ValidationUtils::isPhoneE164($item['input']));
        }

        foreach ($this->fixtures['id']['uuid'] as $item) {
            $this->assertMatchesRegularExpression(
                '/^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i',
                IdUtils::uuid()
            );
            $this->assertSame('uuid-v4', $item['pattern']);
        }
        foreach ($this->fixtures['id']['random_string'] as $item) {
            $value = IdUtils::randomString($item['length']);
            $this->assertSame($item['length'], strlen($value));
            $this->assertMatchesRegularExpression('/^[A-Za-z0-9]+$/', $value);
        }

        foreach ($this->fixtures['path']['join_path'] as $item) {
            $this->assertSame($item['output'], PathUtils::joinPath(...$item['segments']));
        }
        foreach ($this->fixtures['path']['normalize_path'] as $item) {
            $this->assertSame($item['output'], PathUtils::normalizePath($item['input']));
        }

        foreach ($this->fixtures['i18n']['format_number_locale'] as $item) {
            $this->assertSame($item['output'], I18nUtils::formatNumberLocale($item['value'], $item['locale'], $item['decimals']));
        }
        foreach ($this->fixtures['i18n']['format_datetime_locale'] as $item) {
            $formatted = I18nUtils::formatDatetimeLocaleStr($item['input'], $item['locale']);
            $this->assertNotNull($formatted);
            $this->assertStringContainsString($item['contains'], $formatted);
        }
        foreach ($this->fixtures['i18n']['parse_number_locale'] as $item) {
            $this->assertSame($item['output'], I18nUtils::parseNumberLocale($item['input'], $item['locale']));
        }

        foreach ($this->fixtures['optional']['coalesce'] as $item) {
            $this->assertSame($item['output'], OptionalUtils::coalesce(...$item['values']));
        }
        foreach ($this->fixtures['optional']['default_if_blank'] as $item) {
            $this->assertSame($item['output'], OptionalUtils::defaultIfBlank($item['input'], $item['default']));
        }

        foreach ($this->fixtures['result']['ok'] as $item) {
            $result = ResultValue::ok($item['value']);
            $this->assertSame($item['is_ok'], $result->isOk());
            $this->assertSame($item['is_err'], $result->isErr());
        }
        foreach ($this->fixtures['result']['err'] as $item) {
            $result = ResultValue::err($item['message']);
            $this->assertSame($item['is_ok'], $result->isOk());
            $this->assertSame($item['is_err'], $result->isErr());
        }
        foreach ($this->fixtures['result']['is_ok'] as $item) {
            $result = $item['kind'] === 'ok' ? ResultValue::ok($item['value']) : ResultValue::err($item['message']);
            $this->assertSame($item['output'], $result->isOk());
        }
        foreach ($this->fixtures['result']['is_err'] as $item) {
            $result = $item['kind'] === 'ok' ? ResultValue::ok($item['value']) : ResultValue::err($item['message']);
            $this->assertSame($item['output'], $result->isErr());
        }
        foreach ($this->fixtures['result']['unwrap_or'] as $item) {
            $result = $item['kind'] === 'ok' ? ResultValue::ok($item['value']) : ResultValue::err($item['message']);
            $this->assertSame($item['output'], $result->unwrapOr($item['default']));
        }
        foreach ($this->fixtures['result']['map'] as $item) {
            $mapped = ResultValue::ok($item['value'])->map(static fn (int $value): int => $value * 2);
            $this->assertSame($item['output'], $mapped->value);
        }

        foreach ($this->fixtures['boolean']['parse_bool'] as $item) {
            $this->assertSame($item['output'], BooleanUtils::parseBool($item['input']));
        }
        foreach ($this->fixtures['boolean']['is_truthy'] as $item) {
            $this->assertSame($item['output'], BooleanUtils::isTruthy($item['input']));
        }

        foreach ($this->fixtures['compare']['deep_equal'] as $item) {
            $this->assertSame($item['output'], CompareUtils::deepEqual($item['left'], $item['right']));
        }
        foreach ($this->fixtures['compare']['deep_clone'] as $item) {
            $cloned = CompareUtils::deepClone($item['input']);
            $this->assertSame($item['output'], $cloned);
            $cloned['b'][1]['c'] = 99;
            $this->assertSame(3, $item['input']['b'][1]['c']);
        }

        foreach ($this->fixtures['currency']['is_currency_code'] as $item) {
            $this->assertSame($item['output'], CurrencyUtils::isCurrencyCode($item['input']));
        }
        foreach ($this->fixtures['currency']['minor_unit_exponent'] as $item) {
            $this->assertSame($item['output'], CurrencyUtils::minorUnitExponent($item['code']));
        }
        foreach ($this->fixtures['currency']['to_minor_units'] as $item) {
            $this->assertSame($item['output'], CurrencyUtils::toMinorUnits($item['amount'], $item['code']));
        }
        foreach ($this->fixtures['currency']['from_minor_units'] as $item) {
            $this->assertSame($item['output'], CurrencyUtils::fromMinorUnits($item['minor'], $item['code']));
        }
        foreach ($this->fixtures['currency']['format_currency'] as $item) {
            $this->assertSame($item['output'], CurrencyUtils::formatCurrency($item['amount'], $item['code'], $item['locale']));
        }

        foreach ($this->fixtures['bloom']['create'] as $item) {
            $filter = BloomUtils::create($item['expected_items'], $item['false_positive_rate']);
            $this->assertSame($item['bit_count'], $filter->bitCount);
            $this->assertSame($item['hash_count'], $filter->hashCount);
        }
        foreach ($this->fixtures['bloom']['estimate_bit_count'] as $item) {
            $this->assertSame($item['output'], BloomUtils::estimateBitCount($item['expected_items'], $item['false_positive_rate']));
        }
        foreach ($this->fixtures['bloom']['estimate_hash_count'] as $item) {
            $this->assertSame($item['output'], BloomUtils::estimateHashCount($item['expected_items'], $item['bit_count']));
        }
        foreach ($this->fixtures['bloom']['might_contain'] as $item) {
            $filter = BloomUtils::create(128, 0.01);
            foreach ($item['added'] as $value) {
                BloomUtils::add($filter, $value);
            }
            $this->assertSame($item['present_output'], BloomUtils::mightContain($filter, $item['present']));
            $this->assertSame($item['absent_output'], BloomUtils::mightContain($filter, $item['absent']));
        }

        foreach ($this->fixtures['bytes']['format_bytes'] as $item) {
            $this->assertSame($item['output'], BytesUtils::formatBytes($item['value'], $item['decimals']));
        }
    }
}
