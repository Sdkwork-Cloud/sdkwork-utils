using System.Globalization;

namespace Sdkwork.Utils;

/// <summary>
/// Money display formatting aligned with industry Intl.NumberFormat conventions.
/// Modes: symbol, narrow_symbol, code, name, decimal, accounting, compact.
/// Symbol placement: en-US/zh-CN/ja-JP/ko-KR prefix without space; de-DE/fr-FR/it-IT/es-ES/ru-RU
/// suffix with a single space. Rounding is half-up on the shortest decimal representation.
/// </summary>
public static class MoneyUtils
{
    private sealed record CompactUnit(int Exponent, string Unit);

    private sealed record LocaleRules(
        bool Prefix, string Decimal, string Grouping, bool NameSpace, CompactUnit[] Compact);

    private static readonly HashSet<string> Modes = new(StringComparer.Ordinal)
    {
        "symbol", "narrow_symbol", "code", "name", "decimal", "accounting", "compact",
    };

    private static readonly HashSet<string> Signs = new(StringComparer.Ordinal)
    {
        "auto", "always", "never", "except_zero",
    };

    private static readonly Dictionary<string, string> CurrencySymbols = new(StringComparer.Ordinal)
    {
        ["USD"] = "$",
        ["EUR"] = "€",
        ["GBP"] = "£",
        ["CNY"] = "¥",
        ["JPY"] = "¥",
        ["KRW"] = "₩",
        ["HKD"] = "HK$",
        ["TWD"] = "NT$",
        ["CHF"] = "CHF",
        ["CAD"] = "CA$",
        ["AUD"] = "A$",
        ["INR"] = "₹",
        ["BHD"] = "BHD",
        ["KWD"] = "KWD",
    };

    private static readonly CompactUnit[] EnCompact =
    {
        new(12, "T"), new(9, "B"), new(6, "M"), new(3, "K"),
    };
    private static readonly CompactUnit[] ZhCompact =
    {
        new(12, "兆"), new(8, "亿"), new(4, "万"),
    };
    private static readonly CompactUnit[] JaCompact =
    {
        new(12, "兆"), new(8, "億"), new(4, "万"),
    };
    private static readonly CompactUnit[] KoCompact =
    {
        new(12, "조"), new(8, "억"), new(4, "만"),
    };
    private static readonly CompactUnit[] DeCompact =
    {
        new(12, "Bio."), new(9, "Mrd."), new(6, "Mio."), new(3, "Tsd."),
    };
    private static readonly CompactUnit[] FrCompact =
    {
        new(12, "B"), new(9, "Md"), new(6, "M"), new(3, "k"),
    };
    private static readonly CompactUnit[] ItCompact =
    {
        new(12, "Bio."), new(9, "Mrd."), new(6, "M"), new(3, "k"),
    };
    private static readonly CompactUnit[] EsCompact =
    {
        new(12, "T"), new(9, "B"), new(6, "M"), new(3, "k"),
    };
    private static readonly CompactUnit[] RuCompact =
    {
        new(12, "трлн"), new(9, "млрд"), new(6, "млн"), new(3, "тыс."),
    };

    private static readonly Dictionary<string, LocaleRules> LocaleRulesMap = new(StringComparer.Ordinal)
    {
        ["en-us"] = new(true, ".", ",", true, EnCompact),
        ["zh-cn"] = new(true, ".", ",", false, ZhCompact),
        ["ja-jp"] = new(true, ".", ",", false, JaCompact),
        ["ko-kr"] = new(true, ".", ",", false, KoCompact),
        ["de-de"] = new(false, ",", ".", true, DeCompact),
        ["fr-fr"] = new(false, ",", " ", true, FrCompact),
        ["it-it"] = new(false, ",", ".", true, ItCompact),
        ["es-es"] = new(false, ",", ".", true, EsCompact),
        ["ru-ru"] = new(false, ",", " ", true, RuCompact),
    };

    private static readonly Dictionary<string, Dictionary<string, string>> CurrencyNames = new(StringComparer.Ordinal)
    {
        ["en-us"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "US dollars", ["EUR"] = "euros", ["GBP"] = "British pounds",
            ["CNY"] = "Chinese yuan", ["JPY"] = "Japanese yen", ["KRW"] = "South Korean won",
            ["HKD"] = "Hong Kong dollars", ["TWD"] = "New Taiwan dollars", ["CHF"] = "Swiss francs",
            ["CAD"] = "Canadian dollars", ["AUD"] = "Australian dollars", ["INR"] = "Indian rupees",
            ["BHD"] = "Bahraini dinars", ["KWD"] = "Kuwaiti dinars",
        },
        ["zh-cn"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "美元", ["EUR"] = "欧元", ["GBP"] = "英镑", ["CNY"] = "人民币", ["JPY"] = "日元",
            ["KRW"] = "韩元", ["HKD"] = "港币", ["TWD"] = "新台币", ["CHF"] = "瑞士法郎",
            ["CAD"] = "加拿大元", ["AUD"] = "澳大利亚元", ["INR"] = "印度卢比", ["BHD"] = "巴林第纳尔",
            ["KWD"] = "科威特第纳尔",
        },
        ["de-de"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "US-Dollar", ["EUR"] = "Euro", ["GBP"] = "Britisches Pfund",
            ["CNY"] = "Chinesischer Yuan", ["JPY"] = "Japanischer Yen", ["KRW"] = "Südkoreanischer Won",
            ["HKD"] = "Hongkong-Dollar", ["TWD"] = "Neuer Taiwan-Dollar", ["CHF"] = "Schweizer Franken",
            ["CAD"] = "Kanadischer Dollar", ["AUD"] = "Australischer Dollar", ["INR"] = "Indische Rupie",
            ["BHD"] = "Bahrainischer Dinar", ["KWD"] = "Kuwaitischer Dinar",
        },
        ["fr-fr"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "dollar américain", ["EUR"] = "euro", ["GBP"] = "livre sterling",
            ["CNY"] = "yuan chinois", ["JPY"] = "yen japonais", ["KRW"] = "won sud-coréen",
            ["HKD"] = "dollar de Hong Kong", ["TWD"] = "nouveau dollar de Taïwan",
            ["CHF"] = "franc suisse", ["CAD"] = "dollar canadien", ["AUD"] = "dollar australien",
            ["INR"] = "roupie indienne", ["BHD"] = "dinar bahreïni", ["KWD"] = "dinar koweïtien",
        },
        ["it-it"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "dollaro statunitense", ["EUR"] = "euro", ["GBP"] = "sterlina britannica",
            ["CNY"] = "yuan cinese", ["JPY"] = "yen giapponese", ["KRW"] = "won sudcoreano",
            ["HKD"] = "dollaro di Hong Kong", ["TWD"] = "nuovo dollaro taiwanese",
            ["CHF"] = "franco svizzero", ["CAD"] = "dollaro canadese", ["AUD"] = "dollaro australiano",
            ["INR"] = "rupia indiana", ["BHD"] = "dinaro bahreinita", ["KWD"] = "dinaro kuwaitiano",
        },
        ["es-es"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "dólar estadounidense", ["EUR"] = "euro", ["GBP"] = "libra esterlina",
            ["CNY"] = "yuan chino", ["JPY"] = "yen japonés", ["KRW"] = "won surcoreano",
            ["HKD"] = "dólar de Hong Kong", ["TWD"] = "nuevo dólar taiwanés", ["CHF"] = "franco suizo",
            ["CAD"] = "dólar canadiense", ["AUD"] = "dólar australiano", ["INR"] = "rupia india",
            ["BHD"] = "dinar bahreiní", ["KWD"] = "dinar kuwaití",
        },
        ["ja-jp"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "米ドル", ["EUR"] = "ユーロ", ["GBP"] = "英ポンド", ["CNY"] = "中国人民元",
            ["JPY"] = "日本円", ["KRW"] = "韓国ウォン", ["HKD"] = "香港ドル", ["TWD"] = "台湾ドル",
            ["CHF"] = "スイスフラン", ["CAD"] = "カナダドル", ["AUD"] = "オーストラリアドル",
            ["INR"] = "インドルピー", ["BHD"] = "バーレーンディナール", ["KWD"] = "クウェートディナール",
        },
        ["ko-kr"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "미국 달러", ["EUR"] = "유로", ["GBP"] = "영국 파운드", ["CNY"] = "중국 위안",
            ["JPY"] = "일본 엔", ["KRW"] = "대한민국 원", ["HKD"] = "홍콩 달러", ["TWD"] = "신 대만 달러",
            ["CHF"] = "스위스 프랑", ["CAD"] = "캐나다 달러", ["AUD"] = "호주 달러", ["INR"] = "인도 루피",
            ["BHD"] = "바레인 디나르", ["KWD"] = "쿠웨이트 디나르",
        },
        ["ru-ru"] = new(StringComparer.Ordinal)
        {
            ["USD"] = "доллар США", ["EUR"] = "евро", ["GBP"] = "британский фунт",
            ["CNY"] = "китайский юань", ["JPY"] = "японская иена", ["KRW"] = "южнокорейская вона",
            ["HKD"] = "гонконгский доллар", ["TWD"] = "новый тайваньский доллар",
            ["CHF"] = "швейцарский франк", ["CAD"] = "канадский доллар",
            ["AUD"] = "австралийский доллар", ["INR"] = "индийская рупия",
            ["BHD"] = "бахрейнский динар", ["KWD"] = "кувейтский динар",
        },
    };

    private static string? LookupCurrency(string? currency)
    {
        if (currency == null)
        {
            return null;
        }
        var normalized = currency.Trim();
        if (normalized.Length != 3 || normalized != normalized.ToUpperInvariant()
            || !normalized.All(char.IsLetter))
        {
            return null;
        }
        return CurrencySymbols.ContainsKey(normalized) ? normalized : null;
    }

    private static string LocaleKey(string? locale)
    {
        if (locale == null)
        {
            return "en-us";
        }
        var normalized = locale.Trim().ToLowerInvariant();
        if (LocaleRulesMap.ContainsKey(normalized))
        {
            return normalized;
        }
        var language = normalized.Split('-')[0];
        foreach (var key in LocaleRulesMap.Keys)
        {
            if (key.Split('-')[0] == language)
            {
                return key;
            }
        }
        return "en-us";
    }

    private static LocaleRules Rules(string? locale) => LocaleRulesMap[LocaleKey(locale)];

    private static Dictionary<string, string> Names(string? locale) => CurrencyNames[LocaleKey(locale)];

    private sealed record ParsedValue(bool Negative, bool IsZero, string AbsDecimal);

    private static ParsedValue? ParseValue(double? value)
    {
        if (value == null || double.IsNaN(value.Value) || double.IsInfinity(value.Value))
        {
            return null;
        }
        var negative = value.Value < 0;
        var abs = Math.Abs(value.Value);
        if (abs == 0)
        {
            return new ParsedValue(false, true, "0");
        }
        return new ParsedValue(negative, false, ExpandExponent(abs.ToString("R", CultureInfo.InvariantCulture)));
    }

    private static string ExpandExponent(string value)
    {
        var exponentIndex = value.IndexOfAny(new[] { 'e', 'E' });
        if (exponentIndex < 0)
        {
            return value;
        }
        var mantissa = value.Substring(0, exponentIndex);
        var exponent = int.Parse(value.Substring(exponentIndex + 1), CultureInfo.InvariantCulture);
        var dotIndex = mantissa.IndexOf('.');
        string intPart;
        string fracPart;
        if (dotIndex < 0)
        {
            intPart = mantissa;
            fracPart = "";
        }
        else
        {
            intPart = mantissa.Substring(0, dotIndex);
            fracPart = mantissa.Substring(dotIndex + 1);
        }
        var digits = intPart + fracPart;
        var pointIndex = intPart.Length + exponent;
        if (pointIndex <= 0)
        {
            return "0." + new string('0', -pointIndex) + digits;
        }
        if (pointIndex >= digits.Length)
        {
            return digits + new string('0', pointIndex - digits.Length);
        }
        return digits.Substring(0, pointIndex) + "." + digits.Substring(pointIndex);
    }

    private static (string IntPart, string FracPart) SplitDecimal(string absDecimal)
    {
        var index = absDecimal.IndexOf('.');
        return index < 0 ? (absDecimal, "") : (absDecimal.Substring(0, index), absDecimal.Substring(index + 1));
    }

    private static (string IntPart, string FracPart) IncrementDecimal(string intPart, string fracPart)
    {
        var digits = (intPart + fracPart).ToCharArray();
        var index = digits.Length - 1;
        while (index >= 0 && digits[index] == '9')
        {
            digits[index] = '0';
            index--;
        }
        string incremented;
        if (index >= 0)
        {
            digits[index] = (char)(digits[index] + 1);
            incremented = new string(digits);
        }
        else
        {
            incremented = "1" + new string(digits);
        }
        var cut = incremented.Length - fracPart.Length;
        return (incremented.Substring(0, cut), incremented.Substring(cut));
    }

    private static (string IntPart, string FracPart) RoundDecimal(string absDecimal, int maxFraction)
    {
        var (intPart, fracPart) = SplitDecimal(absDecimal);
        if (fracPart.Length <= maxFraction)
        {
            return (intPart, fracPart.PadRight(maxFraction, '0'));
        }
        var keep = fracPart.Substring(0, maxFraction);
        return fracPart[maxFraction] >= '5' ? IncrementDecimal(intPart, keep) : (intPart, keep);
    }

    private static string TrimFraction(string fracPart, int minFraction)
    {
        var end = fracPart.Length;
        while (end > minFraction && fracPart[end - 1] == '0')
        {
            end--;
        }
        return fracPart.Substring(0, end);
    }

    private static string GroupInteger(string intPart, string grouping, bool useGrouping)
    {
        if (!useGrouping)
        {
            return intPart;
        }
        var grouped = new System.Text.StringBuilder();
        for (var index = 0; index < intPart.Length; index++)
        {
            if (index > 0 && (intPart.Length - index) % 3 == 0)
            {
                grouped.Append(grouping);
            }
            grouped.Append(intPart[index]);
        }
        return grouped.ToString();
    }

    private static string ShiftDecimalPoint(string absDecimal, int exponent)
    {
        var (intPart, fracPart) = SplitDecimal(absDecimal);
        var digits = intPart + fracPart;
        var pointIndex = intPart.Length - exponent;
        if (pointIndex <= 0)
        {
            return "0." + new string('0', -pointIndex) + digits;
        }
        if (pointIndex >= digits.Length)
        {
            return digits + new string('0', pointIndex - digits.Length);
        }
        return digits.Substring(0, pointIndex) + "." + digits.Substring(pointIndex);
    }

    private static string FormatCompactBody(ParsedValue parsed, LocaleRules rules)
    {
        if (parsed.IsZero)
        {
            return "0";
        }
        var intLength = SplitDecimal(parsed.AbsDecimal).IntPart.Length;
        var unitIndex = -1;
        for (var index = 0; index < rules.Compact.Length; index++)
        {
            if (intLength > rules.Compact[index].Exponent)
            {
                unitIndex = index;
                break;
            }
        }
        if (unitIndex < 0)
        {
            var (roundedInt, roundedFrac) = RoundDecimal(parsed.AbsDecimal, 1);
            var trimmed = TrimFraction(roundedFrac, 0);
            return trimmed.Length == 0 ? roundedInt : roundedInt + "." + trimmed;
        }
        var unit = rules.Compact[unitIndex];
        var (scaledInt, scaledFrac) = RoundDecimal(ShiftDecimalPoint(parsed.AbsDecimal, unit.Exponent), 1);
        if (scaledInt.Length > 1 && unitIndex + 1 < rules.Compact.Length)
        {
            var nextUnit = rules.Compact[unitIndex + 1];
            var escalated = RoundDecimal(ShiftDecimalPoint(parsed.AbsDecimal, nextUnit.Exponent), 1);
            scaledInt = escalated.IntPart;
            scaledFrac = escalated.FracPart;
            unit = nextUnit;
        }
        var trimmedScaled = TrimFraction(scaledFrac, 0);
        var body = trimmedScaled.Length == 0 ? scaledInt : scaledInt + "." + trimmedScaled;
        return body + unit.Unit;
    }

    private static string SignPrefix(bool negative, bool isZero, string sign) => sign switch
    {
        "always" => negative ? "-" : "+",
        "never" => "",
        "except_zero" => negative ? "-" : isZero ? "" : "+",
        _ => negative ? "-" : "",
    };

    private static (int Min, int Max) DefaultFraction(string mode) => mode switch
    {
        "compact" => (0, 1),
        "decimal" => (0, 2),
        _ => (2, 2),
    };

    private static string? FormatInternal(
        double? value,
        string? currency,
        string? locale,
        string? mode,
        int? minFraction,
        int? maxFraction,
        string? sign,
        bool? useGrouping)
    {
        var code = LookupCurrency(currency);
        if (code == null || mode == null || !Modes.Contains(mode))
        {
            return null;
        }
        if (sign != null && !Signs.Contains(sign))
        {
            return null;
        }
        var parsed = ParseValue(value);
        if (parsed == null)
        {
            return null;
        }

        int resolvedMin;
        int resolvedMax;
        if (minFraction == null && maxFraction == null)
        {
            (resolvedMin, resolvedMax) = DefaultFraction(mode);
        }
        else if (minFraction == null || maxFraction == null)
        {
            return null;
        }
        else
        {
            if (minFraction.Value < 0 || maxFraction.Value > 18 || minFraction.Value > maxFraction.Value)
            {
                return null;
            }
            (resolvedMin, resolvedMax) = mode == "compact" ? (0, 1) : (minFraction.Value, maxFraction.Value);
        }

        var resolvedGrouping = useGrouping ?? true;
        var resolvedSign = sign ?? "auto";
        var rules = Rules(locale);
        var symbol = CurrencySymbols[code];

        if (mode == "compact")
        {
            var body = FormatCompactBody(parsed, rules);
            var signText = SignPrefix(parsed.Negative, parsed.IsZero, resolvedSign);
            return rules.Prefix ? signText + symbol + body : signText + body + " " + symbol;
        }

        var (roundedInt, roundedFrac) = RoundDecimal(parsed.AbsDecimal, resolvedMax);
        var trimmed = TrimFraction(roundedFrac, resolvedMin);
        var grouped = GroupInteger(roundedInt, rules.Grouping, resolvedGrouping);
        var body2 = trimmed.Length == 0 ? grouped : grouped + rules.Decimal + trimmed;
        var signText2 = SignPrefix(parsed.Negative, parsed.IsZero, resolvedSign);

        if (mode == "decimal")
        {
            return signText2 + body2;
        }

        if (mode == "accounting")
        {
            if (parsed.Negative && rules.Prefix)
            {
                return "(" + symbol + body2 + ")";
            }
            if (parsed.Negative && !rules.Prefix)
            {
                return "-" + body2 + " " + symbol;
            }
            return rules.Prefix ? symbol + body2 : body2 + " " + symbol;
        }

        if (mode == "code")
        {
            return rules.Prefix ? signText2 + code + " " + body2 : signText2 + body2 + " " + code;
        }

        if (mode == "name")
        {
            var name = Names(locale).TryGetValue(code, out var localized) ? localized : "US dollars";
            var separator = rules.NameSpace ? " " : "";
            return signText2 + body2 + separator + name;
        }

        return rules.Prefix ? signText2 + symbol + body2 : signText2 + body2 + " " + symbol;
    }

    public static string? MoneySymbol(string currency)
    {
        var code = LookupCurrency(currency);
        return code == null ? null : CurrencySymbols[code];
    }

    public static string? FormatMoney(double value, string currency, string locale, string mode) =>
        FormatInternal(value, currency, locale, mode, null, null, null, null);

    public static string? FormatMoneyDigits(
        double value, string currency, string locale, string mode, int minFraction, int maxFraction) =>
        FormatInternal(value, currency, locale, mode, minFraction, maxFraction, null, null);

    public static string? FormatMoneyMinorUnits(long minor, string currency, string locale, string mode)
    {
        if (mode == "compact" || !Modes.Contains(mode))
        {
            return null;
        }
        var exponent = CurrencyUtils.MinorUnitExponent(currency);
        if (exponent == null)
        {
            return null;
        }
        var major = minor / Math.Pow(10, exponent.Value);
        return FormatInternal(major, currency, locale, mode, exponent.Value, exponent.Value, null, null);
    }

    public static string? FormatMoneyOptions(
        double value, string currency, string locale, string mode,
        int minFraction, int maxFraction, string sign, bool useGrouping) =>
        FormatInternal(value, currency, locale, mode, minFraction, maxFraction, sign, useGrouping);
}
