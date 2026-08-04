<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

/**
 * Money display formatting aligned with industry Intl.NumberFormat conventions.
 *
 * Modes: symbol, narrow_symbol, code, name, decimal, accounting, compact.
 * Symbol placement: en-US/zh-CN/ja-JP/ko-KR prefix without space; de-DE/fr-FR/it-IT/es-ES/ru-RU
 * suffix with a single space. Rounding is half-up on the shortest decimal representation.
 */
final class MoneyUtils
{
    /** @var array<string, string> */
    private const CURRENCY_SYMBOLS = [
        'USD' => '$',
        'EUR' => '€',
        'GBP' => '£',
        'CNY' => '¥',
        'JPY' => '¥',
        'KRW' => '₩',
        'HKD' => 'HK$',
        'TWD' => 'NT$',
        'CHF' => 'CHF',
        'CAD' => 'CA$',
        'AUD' => 'A$',
        'INR' => '₹',
        'BHD' => 'BHD',
        'KWD' => 'KWD',
    ];

    /** @var array<string, true> */
    private const MODES = [
        'symbol' => true,
        'narrow_symbol' => true,
        'code' => true,
        'name' => true,
        'decimal' => true,
        'accounting' => true,
        'compact' => true,
    ];

    /** @var array<string, true> */
    private const SIGNS = [
        'auto' => true,
        'always' => true,
        'never' => true,
        'except_zero' => true,
    ];

    /**
     * @var array<string, array{bool, string, string, bool, array<int, array{int, string}>}>
     *      prefix, decimal separator, grouping separator, name separator, compact units
     */
    private const LOCALE_RULES = [
        'en-us' => [true, '.', ',', true, [[12, 'T'], [9, 'B'], [6, 'M'], [3, 'K']]],
        'zh-cn' => [true, '.', ',', false, [[12, '兆'], [8, '亿'], [4, '万']]],
        'ja-jp' => [true, '.', ',', false, [[12, '兆'], [8, '億'], [4, '万']]],
        'ko-kr' => [true, '.', ',', false, [[12, '조'], [8, '억'], [4, '만']]],
        'de-de' => [false, ',', '.', true, [[12, 'Bio.'], [9, 'Mrd.'], [6, 'Mio.'], [3, 'Tsd.']]],
        'fr-fr' => [false, ',', ' ', true, [[12, 'B'], [9, 'Md'], [6, 'M'], [3, 'k']]],
        'it-it' => [false, ',', '.', true, [[12, 'Bio.'], [9, 'Mrd.'], [6, 'M'], [3, 'k']]],
        'es-es' => [false, ',', '.', true, [[12, 'T'], [9, 'B'], [6, 'M'], [3, 'k']]],
        'ru-ru' => [false, ',', ' ', true, [[12, 'трлн'], [9, 'млрд'], [6, 'млн'], [3, 'тыс.']]],
    ];

    /** @var array<string, array<string, string>> */
    private const CURRENCY_NAMES = [
        'en-us' => [
            'USD' => 'US dollars', 'EUR' => 'euros', 'GBP' => 'British pounds',
            'CNY' => 'Chinese yuan', 'JPY' => 'Japanese yen', 'KRW' => 'South Korean won',
            'HKD' => 'Hong Kong dollars', 'TWD' => 'New Taiwan dollars', 'CHF' => 'Swiss francs',
            'CAD' => 'Canadian dollars', 'AUD' => 'Australian dollars', 'INR' => 'Indian rupees',
            'BHD' => 'Bahraini dinars', 'KWD' => 'Kuwaiti dinars',
        ],
        'zh-cn' => [
            'USD' => '美元', 'EUR' => '欧元', 'GBP' => '英镑', 'CNY' => '人民币', 'JPY' => '日元',
            'KRW' => '韩元', 'HKD' => '港币', 'TWD' => '新台币', 'CHF' => '瑞士法郎',
            'CAD' => '加拿大元', 'AUD' => '澳大利亚元', 'INR' => '印度卢比', 'BHD' => '巴林第纳尔',
            'KWD' => '科威特第纳尔',
        ],
        'de-de' => [
            'USD' => 'US-Dollar', 'EUR' => 'Euro', 'GBP' => 'Britisches Pfund',
            'CNY' => 'Chinesischer Yuan', 'JPY' => 'Japanischer Yen', 'KRW' => 'Südkoreanischer Won',
            'HKD' => 'Hongkong-Dollar', 'TWD' => 'Neuer Taiwan-Dollar', 'CHF' => 'Schweizer Franken',
            'CAD' => 'Kanadischer Dollar', 'AUD' => 'Australischer Dollar', 'INR' => 'Indische Rupie',
            'BHD' => 'Bahrainischer Dinar', 'KWD' => 'Kuwaitischer Dinar',
        ],
        'fr-fr' => [
            'USD' => 'dollar américain', 'EUR' => 'euro', 'GBP' => 'livre sterling',
            'CNY' => 'yuan chinois', 'JPY' => 'yen japonais', 'KRW' => 'won sud-coréen',
            'HKD' => 'dollar de Hong Kong', 'TWD' => 'nouveau dollar de Taïwan',
            'CHF' => 'franc suisse', 'CAD' => 'dollar canadien', 'AUD' => 'dollar australien',
            'INR' => 'roupie indienne', 'BHD' => 'dinar bahreïni', 'KWD' => 'dinar koweïtien',
        ],
        'it-it' => [
            'USD' => 'dollaro statunitense', 'EUR' => 'euro', 'GBP' => 'sterlina britannica',
            'CNY' => 'yuan cinese', 'JPY' => 'yen giapponese', 'KRW' => 'won sudcoreano',
            'HKD' => 'dollaro di Hong Kong', 'TWD' => 'nuovo dollaro taiwanese',
            'CHF' => 'franco svizzero', 'CAD' => 'dollaro canadese', 'AUD' => 'dollaro australiano',
            'INR' => 'rupia indiana', 'BHD' => 'dinaro bahreinita', 'KWD' => 'dinaro kuwaitiano',
        ],
        'es-es' => [
            'USD' => 'dólar estadounidense', 'EUR' => 'euro', 'GBP' => 'libra esterlina',
            'CNY' => 'yuan chino', 'JPY' => 'yen japonés', 'KRW' => 'won surcoreano',
            'HKD' => 'dólar de Hong Kong', 'TWD' => 'nuevo dólar taiwanés', 'CHF' => 'franco suizo',
            'CAD' => 'dólar canadiense', 'AUD' => 'dólar australiano', 'INR' => 'rupia india',
            'BHD' => 'dinar bahreiní', 'KWD' => 'dinar kuwaití',
        ],
        'ja-jp' => [
            'USD' => '米ドル', 'EUR' => 'ユーロ', 'GBP' => '英ポンド', 'CNY' => '中国人民元',
            'JPY' => '日本円', 'KRW' => '韓国ウォン', 'HKD' => '香港ドル', 'TWD' => '台湾ドル',
            'CHF' => 'スイスフラン', 'CAD' => 'カナダドル', 'AUD' => 'オーストラリアドル',
            'INR' => 'インドルピー', 'BHD' => 'バーレーンディナール', 'KWD' => 'クウェートディナール',
        ],
        'ko-kr' => [
            'USD' => '미국 달러', 'EUR' => '유로', 'GBP' => '영국 파운드', 'CNY' => '중국 위안',
            'JPY' => '일본 엔', 'KRW' => '대한민국 원', 'HKD' => '홍콩 달러', 'TWD' => '신 대만 달러',
            'CHF' => '스위스 프랑', 'CAD' => '캐나다 달러', 'AUD' => '호주 달러', 'INR' => '인도 루피',
            'BHD' => '바레인 디나르', 'KWD' => '쿠웨이트 디나르',
        ],
        'ru-ru' => [
            'USD' => 'доллар США', 'EUR' => 'евро', 'GBP' => 'британский фунт',
            'CNY' => 'китайский юань', 'JPY' => 'японская иена', 'KRW' => 'южнокорейская вона',
            'HKD' => 'гонконгский доллар', 'TWD' => 'новый тайваньский доллар',
            'CHF' => 'швейцарский франк', 'CAD' => 'канадский доллар',
            'AUD' => 'австралийский доллар', 'INR' => 'индийская рупия',
            'BHD' => 'бахрейнский динар', 'KWD' => 'кувейтский динар',
        ],
    ];

    private static function lookupCurrency(?string $currency): ?string
    {
        if ($currency === null) {
            return null;
        }
        $normalized = trim($currency);
        if (strlen($normalized) !== 3 || $normalized !== strtoupper($normalized) || !ctype_alpha($normalized)) {
            return null;
        }
        return isset(self::CURRENCY_SYMBOLS[$normalized]) ? $normalized : null;
    }

    private static function localeKey(?string $locale): string
    {
        if ($locale === null) {
            return 'en-us';
        }
        $normalized = strtolower(trim($locale));
        if (isset(self::LOCALE_RULES[$normalized])) {
            return $normalized;
        }
        $language = explode('-', $normalized)[0];
        foreach (array_keys(self::LOCALE_RULES) as $key) {
            if (explode('-', $key)[0] === $language) {
                return $key;
            }
        }
        return 'en-us';
    }

    /** @return array{bool, string, string, bool, array<int, array{int, string}>} */
    private static function rules(?string $locale): array
    {
        return self::LOCALE_RULES[self::localeKey($locale)];
    }

    /** @return array<string, string> */
    private static function names(?string $locale): array
    {
        return self::CURRENCY_NAMES[self::localeKey($locale)];
    }

    /** @return array{bool, bool, string}|null negative, isZero, absDecimal */
    private static function parseValue(?float $value): ?array
    {
        if ($value === null || !is_finite($value)) {
            return null;
        }
        $negative = $value < 0.0;
        $abs = abs($value);
        if ($abs == 0.0) {
            return [false, true, '0'];
        }
        return [$negative, false, self::expandExponent((string) $abs)];
    }

    private static function expandExponent(string $value): string
    {
        $exponentIndex = stripos($value, 'e');
        if ($exponentIndex === false) {
            return $value;
        }
        $mantissa = substr($value, 0, $exponentIndex);
        $exponent = (int) substr($value, $exponentIndex + 1);
        $dotIndex = strpos($mantissa, '.');
        if ($dotIndex === false) {
            $intPart = $mantissa;
            $fracPart = '';
        } else {
            $intPart = substr($mantissa, 0, $dotIndex);
            $fracPart = substr($mantissa, $dotIndex + 1);
        }
        $digits = $intPart . $fracPart;
        $pointIndex = strlen($intPart) + $exponent;
        if ($pointIndex <= 0) {
            return '0.' . str_repeat('0', -$pointIndex) . $digits;
        }
        if ($pointIndex >= strlen($digits)) {
            return $digits . str_repeat('0', $pointIndex - strlen($digits));
        }
        return substr($digits, 0, $pointIndex) . '.' . substr($digits, $pointIndex);
    }

    /** @return array{string, string} */
    private static function splitDecimal(string $absDecimal): array
    {
        $dotIndex = strpos($absDecimal, '.');
        return $dotIndex === false
            ? [$absDecimal, '']
            : [substr($absDecimal, 0, $dotIndex), substr($absDecimal, $dotIndex + 1)];
    }

    /** @return array{string, string} */
    private static function incrementDecimal(string $intPart, string $fracPart): array
    {
        $digits = str_split($intPart . $fracPart);
        $index = count($digits) - 1;
        while ($index >= 0 && $digits[$index] === '9') {
            $digits[$index] = '0';
            $index--;
        }
        if ($index >= 0) {
            $digits[$index] = (string) ((int) $digits[$index] + 1);
            $incremented = implode('', $digits);
        } else {
            $incremented = '1' . implode('', $digits);
        }
        $cut = strlen($incremented) - strlen($fracPart);
        return [substr($incremented, 0, $cut), substr($incremented, $cut)];
    }

    /** @return array{string, string} */
    private static function roundDecimal(string $absDecimal, int $maxFraction): array
    {
        [$intPart, $fracPart] = self::splitDecimal($absDecimal);
        if (strlen($fracPart) <= $maxFraction) {
            return [$intPart, str_pad($fracPart, $maxFraction, '0')];
        }
        $keep = substr($fracPart, 0, $maxFraction);
        if ($fracPart[$maxFraction] >= '5') {
            return self::incrementDecimal($intPart, $keep);
        }
        return [$intPart, $keep];
    }

    private static function trimFraction(string $fracPart, int $minFraction): string
    {
        $end = strlen($fracPart);
        while ($end > $minFraction && $fracPart[$end - 1] === '0') {
            $end--;
        }
        return substr($fracPart, 0, $end);
    }

    private static function groupInteger(string $intPart, string $grouping, bool $useGrouping): string
    {
        if (!$useGrouping) {
            return $intPart;
        }
        $grouped = '';
        $length = strlen($intPart);
        for ($index = 0; $index < $length; $index++) {
            if ($index > 0 && ($length - $index) % 3 === 0) {
                $grouped .= $grouping;
            }
            $grouped .= $intPart[$index];
        }
        return $grouped;
    }

    private static function shiftDecimalPoint(string $absDecimal, int $exponent): string
    {
        [$intPart, $fracPart] = self::splitDecimal($absDecimal);
        $digits = $intPart . $fracPart;
        $pointIndex = strlen($intPart) - $exponent;
        if ($pointIndex <= 0) {
            return '0.' . str_repeat('0', -$pointIndex) . $digits;
        }
        if ($pointIndex >= strlen($digits)) {
            return $digits . str_repeat('0', $pointIndex - strlen($digits));
        }
        return substr($digits, 0, $pointIndex) . '.' . substr($digits, $pointIndex);
    }

    /** @param array{bool, bool, string} $parsed @param array{bool, string, string, bool, array<int, array{int, string}>} $rules */
    private static function formatCompactBody(array $parsed, array $rules): string
    {
        [, $isZero, $absDecimal] = $parsed;
        if ($isZero) {
            return '0';
        }
        $intLength = strlen(self::splitDecimal($absDecimal)[0]);
        $compactUnits = $rules[4];
        $unitIndex = -1;
        foreach ($compactUnits as $index => [$exponent]) {
            if ($intLength > $exponent) {
                $unitIndex = $index;
                break;
            }
        }
        if ($unitIndex < 0) {
            [$roundedInt, $roundedFrac] = self::roundDecimal($absDecimal, 1);
            $trimmed = self::trimFraction($roundedFrac, 0);
            return $trimmed === '' ? $roundedInt : $roundedInt . '.' . $trimmed;
        }
        [$exponent, $unit] = $compactUnits[$unitIndex];
        [$scaledInt, $scaledFrac] = self::roundDecimal(self::shiftDecimalPoint($absDecimal, $exponent), 1);
        if (strlen($scaledInt) > 1 && isset($compactUnits[$unitIndex + 1])) {
            [$nextExponent, $nextUnit] = $compactUnits[$unitIndex + 1];
            [$scaledInt, $scaledFrac] = self::roundDecimal(self::shiftDecimalPoint($absDecimal, $nextExponent), 1);
            $unit = $nextUnit;
        }
        $trimmedScaled = self::trimFraction($scaledFrac, 0);
        $body = $trimmedScaled === '' ? $scaledInt : $scaledInt . '.' . $trimmedScaled;
        return $body . $unit;
    }

    private static function signPrefix(bool $negative, bool $isZero, string $sign): string
    {
        return match ($sign) {
            'always' => $negative ? '-' : '+',
            'never' => '',
            'except_zero' => $negative ? '-' : ($isZero ? '' : '+'),
            default => $negative ? '-' : '',
        };
    }

    /** @return array{int, int} */
    private static function defaultFraction(string $mode): array
    {
        return match ($mode) {
            'compact' => [0, 1],
            'decimal' => [0, 2],
            default => [2, 2],
        };
    }

    private static function formatInternal(
        ?float $value,
        ?string $currency,
        ?string $locale,
        ?string $mode,
        ?int $minFraction,
        ?int $maxFraction,
        ?string $sign,
        ?bool $useGrouping
    ): ?string {
        $code = self::lookupCurrency($currency);
        if ($code === null || $mode === null || !isset(self::MODES[$mode])) {
            return null;
        }
        if ($sign !== null && !isset(self::SIGNS[$sign])) {
            return null;
        }
        $parsed = self::parseValue($value);
        if ($parsed === null) {
            return null;
        }

        if ($minFraction === null && $maxFraction === null) {
            [$resolvedMin, $resolvedMax] = self::defaultFraction($mode);
        } elseif ($minFraction === null || $maxFraction === null) {
            return null;
        } else {
            if ($minFraction < 0 || $maxFraction > 18 || $minFraction > $maxFraction) {
                return null;
            }
            [$resolvedMin, $resolvedMax] = $mode === 'compact' ? [0, 1] : [$minFraction, $maxFraction];
        }

        $resolvedGrouping = $useGrouping ?? true;
        $resolvedSign = $sign ?? 'auto';
        $rules = self::rules($locale);
        [$prefix, $decimalSeparator, $groupingSeparator, $nameSpace] = $rules;
        [$negative, $isZero, $absDecimal] = $parsed;
        $symbol = self::CURRENCY_SYMBOLS[$code];

        if ($mode === 'compact') {
            $body = self::formatCompactBody($parsed, $rules);
            $signText = self::signPrefix($negative, $isZero, $resolvedSign);
            return $prefix ? $signText . $symbol . $body : $signText . $body . ' ' . $symbol;
        }

        [$roundedInt, $roundedFrac] = self::roundDecimal($absDecimal, $resolvedMax);
        $trimmed = self::trimFraction($roundedFrac, $resolvedMin);
        $grouped = self::groupInteger($roundedInt, $groupingSeparator, $resolvedGrouping);
        $body = $trimmed === '' ? $grouped : $grouped . $decimalSeparator . $trimmed;
        $signText = self::signPrefix($negative, $isZero, $resolvedSign);

        if ($mode === 'decimal') {
            return $signText . $body;
        }

        if ($mode === 'accounting') {
            if ($negative && $prefix) {
                return '(' . $symbol . $body . ')';
            }
            if ($negative && !$prefix) {
                return '-' . $body . ' ' . $symbol;
            }
            return $prefix ? $symbol . $body : $body . ' ' . $symbol;
        }

        if ($mode === 'code') {
            return $prefix ? $signText . $code . ' ' . $body : $signText . $body . ' ' . $code;
        }

        if ($mode === 'name') {
            $name = self::names($locale)[$code] ?? 'US dollars';
            $separator = $nameSpace ? ' ' : '';
            return $signText . $body . $separator . $name;
        }

        return $prefix ? $signText . $symbol . $body : $signText . $body . ' ' . $symbol;
    }

    public static function moneySymbol(string $currency): ?string
    {
        $code = self::lookupCurrency($currency);
        return $code === null ? null : self::CURRENCY_SYMBOLS[$code];
    }

    public static function formatMoney(float $value, string $currency, string $locale, string $mode): ?string
    {
        return self::formatInternal($value, $currency, $locale, $mode, null, null, null, null);
    }

    public static function formatMoneyDigits(
        float $value,
        string $currency,
        string $locale,
        string $mode,
        int $minFraction,
        int $maxFraction
    ): ?string {
        return self::formatInternal($value, $currency, $locale, $mode, $minFraction, $maxFraction, null, null);
    }

    public static function formatMoneyMinorUnits(int $minor, string $currency, string $locale, string $mode): ?string
    {
        if ($mode === 'compact' || !isset(self::MODES[$mode])) {
            return null;
        }
        $exponent = CurrencyUtils::minorUnitExponent($currency);
        if ($exponent === null) {
            return null;
        }
        $major = $minor / (10 ** $exponent);
        return self::formatInternal($major, $currency, $locale, $mode, $exponent, $exponent, null, null);
    }

    public static function formatMoneyOptions(
        float $value,
        string $currency,
        string $locale,
        string $mode,
        int $minFraction,
        int $maxFraction,
        string $sign,
        bool $useGrouping
    ): ?string {
        return self::formatInternal($value, $currency, $locale, $mode, $minFraction, $maxFraction, $sign, $useGrouping);
    }
}
