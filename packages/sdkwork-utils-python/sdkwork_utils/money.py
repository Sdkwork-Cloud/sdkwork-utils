"""Money display formatting aligned with industry Intl.NumberFormat conventions."""

from sdkwork_utils.currency import minor_unit_exponent

_CURRENCY_SYMBOLS: dict[str, str] = {
    "USD": "$",
    "EUR": "€",
    "GBP": "£",
    "CNY": "¥",
    "JPY": "¥",
    "KRW": "₩",
    "HKD": "HK$",
    "TWD": "NT$",
    "CHF": "CHF",
    "CAD": "CA$",
    "AUD": "A$",
    "INR": "₹",
    "BHD": "BHD",
    "KWD": "KWD",
}

_MONEY_MODES = frozenset(
    ["symbol", "narrow_symbol", "code", "name", "decimal", "accounting", "compact"]
)
_MONEY_SIGNS = frozenset(["auto", "always", "never", "except_zero"])

# (prefix, decimal_separator, grouping_separator, name_space, compact_units)
# compact_units: ((exponent, unit), ...) ordered from largest to smallest.
_LOCALE_RULES: dict[str, tuple[bool, str, str, bool, tuple[tuple[int, str], ...]]] = {
    "en-us": (True, ".", ",", True, ((12, "T"), (9, "B"), (6, "M"), (3, "K"))),
    "zh-cn": (True, ".", ",", False, ((12, "兆"), (8, "亿"), (4, "万"))),
    "ja-jp": (True, ".", ",", False, ((12, "兆"), (8, "億"), (4, "万"))),
    "ko-kr": (True, ".", ",", False, ((12, "조"), (8, "억"), (4, "만"))),
    "de-de": (False, ",", ".", True, ((12, "Bio."), (9, "Mrd."), (6, "Mio."), (3, "Tsd."))),
    "fr-fr": (False, ",", " ", True, ((12, "B"), (9, "Md"), (6, "M"), (3, "k"))),
    "it-it": (False, ",", ".", True, ((12, "Bio."), (9, "Mrd."), (6, "M"), (3, "k"))),
    "es-es": (False, ",", ".", True, ((12, "T"), (9, "B"), (6, "M"), (3, "k"))),
    "ru-ru": (False, ",", " ", True, ((12, "трлн"), (9, "млрд"), (6, "млн"), (3, "тыс."))),
}

_CURRENCY_NAMES: dict[str, dict[str, str]] = {
    "en-us": {
        "USD": "US dollars",
        "EUR": "euros",
        "GBP": "British pounds",
        "CNY": "Chinese yuan",
        "JPY": "Japanese yen",
        "KRW": "South Korean won",
        "HKD": "Hong Kong dollars",
        "TWD": "New Taiwan dollars",
        "CHF": "Swiss francs",
        "CAD": "Canadian dollars",
        "AUD": "Australian dollars",
        "INR": "Indian rupees",
        "BHD": "Bahraini dinars",
        "KWD": "Kuwaiti dinars",
    },
    "zh-cn": {
        "USD": "美元",
        "EUR": "欧元",
        "GBP": "英镑",
        "CNY": "人民币",
        "JPY": "日元",
        "KRW": "韩元",
        "HKD": "港币",
        "TWD": "新台币",
        "CHF": "瑞士法郎",
        "CAD": "加拿大元",
        "AUD": "澳大利亚元",
        "INR": "印度卢比",
        "BHD": "巴林第纳尔",
        "KWD": "科威特第纳尔",
    },
    "de-de": {
        "USD": "US-Dollar",
        "EUR": "Euro",
        "GBP": "Britisches Pfund",
        "CNY": "Chinesischer Yuan",
        "JPY": "Japanischer Yen",
        "KRW": "Südkoreanischer Won",
        "HKD": "Hongkong-Dollar",
        "TWD": "Neuer Taiwan-Dollar",
        "CHF": "Schweizer Franken",
        "CAD": "Kanadischer Dollar",
        "AUD": "Australischer Dollar",
        "INR": "Indische Rupie",
        "BHD": "Bahrainischer Dinar",
        "KWD": "Kuwaitischer Dinar",
    },
    "fr-fr": {
        "USD": "dollar américain",
        "EUR": "euro",
        "GBP": "livre sterling",
        "CNY": "yuan chinois",
        "JPY": "yen japonais",
        "KRW": "won sud-coréen",
        "HKD": "dollar de Hong Kong",
        "TWD": "nouveau dollar de Taïwan",
        "CHF": "franc suisse",
        "CAD": "dollar canadien",
        "AUD": "dollar australien",
        "INR": "roupie indienne",
        "BHD": "dinar bahreïni",
        "KWD": "dinar koweïtien",
    },
    "it-it": {
        "USD": "dollaro statunitense",
        "EUR": "euro",
        "GBP": "sterlina britannica",
        "CNY": "yuan cinese",
        "JPY": "yen giapponese",
        "KRW": "won sudcoreano",
        "HKD": "dollaro di Hong Kong",
        "TWD": "nuovo dollaro taiwanese",
        "CHF": "franco svizzero",
        "CAD": "dollaro canadese",
        "AUD": "dollaro australiano",
        "INR": "rupia indiana",
        "BHD": "dinaro bahreinita",
        "KWD": "dinaro kuwaitiano",
    },
    "es-es": {
        "USD": "dólar estadounidense",
        "EUR": "euro",
        "GBP": "libra esterlina",
        "CNY": "yuan chino",
        "JPY": "yen japonés",
        "KRW": "won surcoreano",
        "HKD": "dólar de Hong Kong",
        "TWD": "nuevo dólar taiwanés",
        "CHF": "franco suizo",
        "CAD": "dólar canadiense",
        "AUD": "dólar australiano",
        "INR": "rupia india",
        "BHD": "dinar bahreiní",
        "KWD": "dinar kuwaití",
    },
    "ja-jp": {
        "USD": "米ドル",
        "EUR": "ユーロ",
        "GBP": "英ポンド",
        "CNY": "中国人民元",
        "JPY": "日本円",
        "KRW": "韓国ウォン",
        "HKD": "香港ドル",
        "TWD": "台湾ドル",
        "CHF": "スイスフラン",
        "CAD": "カナダドル",
        "AUD": "オーストラリアドル",
        "INR": "インドルピー",
        "BHD": "バーレーンディナール",
        "KWD": "クウェートディナール",
    },
    "ko-kr": {
        "USD": "미국 달러",
        "EUR": "유로",
        "GBP": "영국 파운드",
        "CNY": "중국 위안",
        "JPY": "일본 엔",
        "KRW": "대한민국 원",
        "HKD": "홍콩 달러",
        "TWD": "신 대만 달러",
        "CHF": "스위스 프랑",
        "CAD": "캐나다 달러",
        "AUD": "호주 달러",
        "INR": "인도 루피",
        "BHD": "바레인 디나르",
        "KWD": "쿠웨이트 디나르",
    },
    "ru-ru": {
        "USD": "доллар США",
        "EUR": "евро",
        "GBP": "британский фунт",
        "CNY": "китайский юань",
        "JPY": "японская иена",
        "KRW": "южнокорейская вона",
        "HKD": "гонконгский доллар",
        "TWD": "новый тайваньский доллар",
        "CHF": "швейцарский франк",
        "CAD": "канадский доллар",
        "AUD": "австралийский доллар",
        "INR": "индийская рупия",
        "BHD": "бахрейнский динар",
        "KWD": "кувейтский динар",
    },
}


def _lookup_currency(currency) -> str | None:
    if not isinstance(currency, str):
        return None
    normalized = currency.strip()
    if len(normalized) != 3 or normalized != normalized.upper() or not normalized.isalpha():
        return None
    return normalized if normalized in _CURRENCY_SYMBOLS else None


def _locale_key(locale) -> str:
    if not isinstance(locale, str):
        return "en-us"
    normalized = locale.strip().lower()
    if normalized in _LOCALE_RULES:
        return normalized
    language = normalized.split("-")[0]
    for key in _LOCALE_RULES:
        if key.split("-")[0] == language:
            return key
    return "en-us"


def _rules(locale) -> tuple[bool, str, str, bool, tuple[tuple[int, str], ...]]:
    return _LOCALE_RULES[_locale_key(locale)]


def _names(locale) -> dict[str, str]:
    return _CURRENCY_NAMES[_locale_key(locale)]


def _expand_exponent(value: str) -> str:
    if "e" not in value and "E" not in value:
        return value
    normalized = value.replace("E", "e")
    mantissa, exponent_text = normalized.split("e")
    exponent = int(exponent_text)
    if "." in mantissa:
        int_part, frac_part = mantissa.split(".")
    else:
        int_part, frac_part = mantissa, ""
    digits = int_part + frac_part
    point_index = len(int_part) + exponent
    if point_index <= 0:
        return "0." + "0" * (-point_index) + digits
    if point_index >= len(digits):
        return digits + "0" * (point_index - len(digits))
    return digits[:point_index] + "." + digits[point_index:]


def _parse_value(value) -> tuple[bool, bool, str] | None:
    if value is None:
        return None
    if isinstance(value, str):
        trimmed = value.strip()
        if trimmed and trimmed.lstrip("+-").isdigit():
            negative = trimmed.startswith("-")
            unsigned = trimmed[1:] if trimmed[:1] in ("+", "-") else trimmed
            int_part = unsigned
            frac_part = ""
            return (negative, set(int_part + frac_part) <= {"0"}, int_part)
        parts = trimmed.split(".", 1) if trimmed else [""]
        if len(parts) == 2 and parts[0] and parts[0].lstrip("+-").isdigit() and parts[1].isdigit():
            negative = parts[0].startswith("-")
            int_part = parts[0][1:] if parts[0][:1] in ("+", "-") else parts[0]
            frac_part = parts[1]
            return (negative, set(int_part + frac_part) <= {"0"}, f"{int_part}.{frac_part}")
        return None
    if isinstance(value, float):
        if value != value or value in (float("inf"), float("-inf")):
            return None
        negative = value < 0
        abs_value = abs(value)
        if abs_value == 0:
            return (False, True, "0")
        return (negative, False, _expand_exponent(repr(abs_value)))
    if isinstance(value, int):
        negative = value < 0
        abs_value = abs(value)
        if abs_value == 0:
            return (False, True, "0")
        return (negative, False, str(abs_value))
    return None


def _split_decimal(abs_decimal: str) -> tuple[str, str]:
    if "." in abs_decimal:
        int_part, frac_part = abs_decimal.split(".", 1)
        return int_part, frac_part
    return abs_decimal, ""


def _increment_decimal(int_part: str, frac_part: str) -> tuple[str, str]:
    digits = list(int_part + frac_part)
    index = len(digits) - 1
    while index >= 0 and digits[index] == "9":
        digits[index] = "0"
        index -= 1
    if index >= 0:
        digits[index] = str(int(digits[index]) + 1)
    else:
        digits.insert(0, "1")
    cut = len(digits) - len(frac_part)
    return "".join(digits[:cut]), "".join(digits[cut:])


def _round_decimal(abs_decimal: str, max_fraction: int) -> tuple[str, str]:
    int_part, frac_part = _split_decimal(abs_decimal)
    if len(frac_part) <= max_fraction:
        return int_part, frac_part.ljust(max_fraction, "0")
    keep = frac_part[:max_fraction]
    if frac_part[max_fraction] >= "5":
        return _increment_decimal(int_part, keep)
    return int_part, keep


def _trim_fraction(frac_part: str, min_fraction: int) -> str:
    end = len(frac_part)
    while end > min_fraction and frac_part[end - 1] == "0":
        end -= 1
    return frac_part[:end]


def _group_integer(int_part: str, grouping: str, use_grouping: bool) -> str:
    if not use_grouping:
        return int_part
    grouped = []
    for index, char in enumerate(reversed(int_part)):
        if index > 0 and index % 3 == 0:
            grouped.append(grouping)
        grouped.append(char)
    return "".join(reversed(grouped))


def _shift_decimal_point(abs_decimal: str, exponent: int) -> str:
    int_part, frac_part = _split_decimal(abs_decimal)
    digits = int_part + frac_part
    point_index = len(int_part) - exponent
    if point_index <= 0:
        return "0." + "0" * (-point_index) + digits
    if point_index >= len(digits):
        return digits + "0" * (point_index - len(digits))
    return digits[:point_index] + "." + digits[point_index:]


def _format_compact_body(parsed: tuple[bool, bool, str], rules) -> str:
    negative, is_zero, abs_decimal = parsed
    if is_zero:
        return "0"
    int_length = len(_split_decimal(abs_decimal)[0])
    unit_index = None
    compact_units = rules[4]
    for index, (exponent, _) in enumerate(compact_units):
        if int_length > exponent:
            unit_index = index
            break
    if unit_index is None:
        int_part, frac_part = _round_decimal(abs_decimal, 1)
        trimmed = _trim_fraction(frac_part, 0)
        return f"{int_part}.{trimmed}" if trimmed else int_part
    exponent, unit = compact_units[unit_index]
    scaled_int, scaled_frac = _round_decimal(_shift_decimal_point(abs_decimal, exponent), 1)
    if len(scaled_int) > 1 and unit_index + 1 < len(compact_units):
        next_exponent, next_unit = compact_units[unit_index + 1]
        scaled_int, scaled_frac = _round_decimal(
            _shift_decimal_point(abs_decimal, next_exponent), 1
        )
        unit = next_unit
    trimmed = _trim_fraction(scaled_frac, 0)
    body = f"{scaled_int}.{trimmed}" if trimmed else scaled_int
    return f"{body}{unit}"


def _sign_prefix(negative: bool, is_zero: bool, sign: str) -> str:
    if sign == "always":
        return "-" if negative else "+"
    if sign == "never":
        return ""
    if sign == "except_zero":
        if negative:
            return "-"
        return "" if is_zero else "+"
    return "-" if negative else ""


def _default_fraction(mode: str) -> tuple[int, int]:
    if mode == "compact":
        return 0, 1
    if mode == "decimal":
        return 0, 2
    return 2, 2


def _format_money_internal(
    value,
    currency,
    locale,
    mode,
    min_fraction,
    max_fraction,
    sign,
    use_grouping,
) -> str | None:
    code = _lookup_currency(currency)
    if code is None or mode not in _MONEY_MODES:
        return None
    if sign is not None and sign not in _MONEY_SIGNS:
        return None
    parsed = _parse_value(value)
    if parsed is None:
        return None

    if min_fraction is None and max_fraction is None:
        min_fraction, max_fraction = _default_fraction(mode)
    elif min_fraction is None or max_fraction is None:
        return None
    else:
        if min_fraction < 0 or max_fraction > 18 or min_fraction > max_fraction:
            return None
        if mode == "compact":
            min_fraction, max_fraction = 0, 1

    if use_grouping is None:
        use_grouping = True
    if sign is None:
        sign = "auto"
    rules = _rules(locale)
    prefix, decimal_sep, grouping_sep, name_space, _compact = rules
    negative, is_zero, abs_decimal = parsed
    symbol = _CURRENCY_SYMBOLS[code]

    if mode == "compact":
        body = _format_compact_body(parsed, rules)
        sign_text = _sign_prefix(negative, is_zero, sign)
        return f"{sign_text}{symbol}{body}" if prefix else f"{sign_text}{body} {symbol}"

    int_part, frac_part = _round_decimal(abs_decimal, max_fraction)
    trimmed = _trim_fraction(frac_part, min_fraction)
    grouped = _group_integer(int_part, grouping_sep, use_grouping)
    body = f"{grouped}{decimal_sep}{trimmed}" if trimmed else grouped
    sign_text = _sign_prefix(negative, is_zero, sign)

    if mode == "decimal":
        return f"{sign_text}{body}"

    if mode == "accounting":
        if negative and prefix:
            return f"({symbol}{body})"
        if negative and not prefix:
            return f"-{body} {symbol}"
        return f"{symbol}{body}" if prefix else f"{body} {symbol}"

    if mode == "code":
        return f"{sign_text}{code} {body}" if prefix else f"{sign_text}{body} {code}"

    if mode == "name":
        name = _names(locale).get(code, "US dollars")
        separator = " " if name_space else ""
        return f"{sign_text}{body}{separator}{name}"

    return f"{sign_text}{symbol}{body}" if prefix else f"{sign_text}{body} {symbol}"


def money_symbol(currency: str) -> str | None:
    code = _lookup_currency(currency)
    return _CURRENCY_SYMBOLS[code] if code else None


def format_money(value, currency: str, locale: str, mode: str) -> str | None:
    return _format_money_internal(value, currency, locale, mode, None, None, None, None)


def format_money_digits(
    value, currency: str, locale: str, mode: str, min_fraction: int, max_fraction: int
) -> str | None:
    return _format_money_internal(
        value, currency, locale, mode, min_fraction, max_fraction, None, None
    )


def format_money_minor_units(
    minor, currency: str, locale: str, mode: str
) -> str | None:
    if not isinstance(minor, int) or isinstance(minor, bool):
        return None
    if mode == "compact" or mode not in _MONEY_MODES:
        return None
    exponent = minor_unit_exponent(currency)
    if exponent is None:
        return None
    major = minor / (10**exponent)
    return _format_money_internal(
        major, currency, locale, mode, exponent, exponent, None, None
    )


def format_money_options(
    value,
    currency: str,
    locale: str,
    mode: str,
    min_fraction: int,
    max_fraction: int,
    sign: str,
    use_grouping: bool,
) -> str | None:
    return _format_money_internal(
        value, currency, locale, mode, min_fraction, max_fraction, sign, use_grouping
    )
