use crate::currency::minor_unit_exponent;

const CURRENCY_SYMBOLS: &[(&str, &str)] = &[
    ("USD", "$"),
    ("EUR", "€"),
    ("GBP", "£"),
    ("CNY", "¥"),
    ("JPY", "¥"),
    ("KRW", "₩"),
    ("HKD", "HK$"),
    ("TWD", "NT$"),
    ("CHF", "CHF"),
    ("CAD", "CA$"),
    ("AUD", "A$"),
    ("INR", "₹"),
    ("BHD", "BHD"),
    ("KWD", "KWD"),
];

const MONEY_MODES: &[&str] = &[
    "symbol",
    "narrow_symbol",
    "code",
    "name",
    "decimal",
    "accounting",
    "compact",
];

const MONEY_SIGNS: &[&str] = &["auto", "always", "never", "except_zero"];

struct CompactUnit {
    exponent: u32,
    unit: &'static str,
}

struct LocaleRules {
    prefix: bool,
    decimal: char,
    grouping: char,
    name_space: bool,
    compact: &'static [CompactUnit],
}

const EN_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "T",
    },
    CompactUnit {
        exponent: 9,
        unit: "B",
    },
    CompactUnit {
        exponent: 6,
        unit: "M",
    },
    CompactUnit {
        exponent: 3,
        unit: "K",
    },
];

const ZH_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "兆",
    },
    CompactUnit {
        exponent: 8,
        unit: "亿",
    },
    CompactUnit {
        exponent: 4,
        unit: "万",
    },
];

const JA_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "兆",
    },
    CompactUnit {
        exponent: 8,
        unit: "億",
    },
    CompactUnit {
        exponent: 4,
        unit: "万",
    },
];

const KO_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "조",
    },
    CompactUnit {
        exponent: 8,
        unit: "억",
    },
    CompactUnit {
        exponent: 4,
        unit: "만",
    },
];

const DE_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "Bio.",
    },
    CompactUnit {
        exponent: 9,
        unit: "Mrd.",
    },
    CompactUnit {
        exponent: 6,
        unit: "Mio.",
    },
    CompactUnit {
        exponent: 3,
        unit: "Tsd.",
    },
];

const FR_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "B",
    },
    CompactUnit {
        exponent: 9,
        unit: "Md",
    },
    CompactUnit {
        exponent: 6,
        unit: "M",
    },
    CompactUnit {
        exponent: 3,
        unit: "k",
    },
];

const IT_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "Bio.",
    },
    CompactUnit {
        exponent: 9,
        unit: "Mrd.",
    },
    CompactUnit {
        exponent: 6,
        unit: "M",
    },
    CompactUnit {
        exponent: 3,
        unit: "k",
    },
];

const ES_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "T",
    },
    CompactUnit {
        exponent: 9,
        unit: "B",
    },
    CompactUnit {
        exponent: 6,
        unit: "M",
    },
    CompactUnit {
        exponent: 3,
        unit: "k",
    },
];

const RU_COMPACT: &[CompactUnit] = &[
    CompactUnit {
        exponent: 12,
        unit: "трлн",
    },
    CompactUnit {
        exponent: 9,
        unit: "млрд",
    },
    CompactUnit {
        exponent: 6,
        unit: "млн",
    },
    CompactUnit {
        exponent: 3,
        unit: "тыс.",
    },
];

const LOCALE_RULES: &[(&str, LocaleRules)] = &[
    (
        "en-us",
        LocaleRules {
            prefix: true,
            decimal: '.',
            grouping: ',',
            name_space: true,
            compact: EN_COMPACT,
        },
    ),
    (
        "zh-cn",
        LocaleRules {
            prefix: true,
            decimal: '.',
            grouping: ',',
            name_space: false,
            compact: ZH_COMPACT,
        },
    ),
    (
        "ja-jp",
        LocaleRules {
            prefix: true,
            decimal: '.',
            grouping: ',',
            name_space: false,
            compact: JA_COMPACT,
        },
    ),
    (
        "ko-kr",
        LocaleRules {
            prefix: true,
            decimal: '.',
            grouping: ',',
            name_space: false,
            compact: KO_COMPACT,
        },
    ),
    (
        "de-de",
        LocaleRules {
            prefix: false,
            decimal: ',',
            grouping: '.',
            name_space: true,
            compact: DE_COMPACT,
        },
    ),
    (
        "fr-fr",
        LocaleRules {
            prefix: false,
            decimal: ',',
            grouping: ' ',
            name_space: true,
            compact: FR_COMPACT,
        },
    ),
    (
        "it-it",
        LocaleRules {
            prefix: false,
            decimal: ',',
            grouping: '.',
            name_space: true,
            compact: IT_COMPACT,
        },
    ),
    (
        "es-es",
        LocaleRules {
            prefix: false,
            decimal: ',',
            grouping: '.',
            name_space: true,
            compact: ES_COMPACT,
        },
    ),
    (
        "ru-ru",
        LocaleRules {
            prefix: false,
            decimal: ',',
            grouping: ' ',
            name_space: true,
            compact: RU_COMPACT,
        },
    ),
];

const CURRENCY_NAMES: &[(&str, &[(&str, &str)])] = &[
    (
        "en-us",
        &[
            ("USD", "US dollars"),
            ("EUR", "euros"),
            ("GBP", "British pounds"),
            ("CNY", "Chinese yuan"),
            ("JPY", "Japanese yen"),
            ("KRW", "South Korean won"),
            ("HKD", "Hong Kong dollars"),
            ("TWD", "New Taiwan dollars"),
            ("CHF", "Swiss francs"),
            ("CAD", "Canadian dollars"),
            ("AUD", "Australian dollars"),
            ("INR", "Indian rupees"),
            ("BHD", "Bahraini dinars"),
            ("KWD", "Kuwaiti dinars"),
        ],
    ),
    (
        "zh-cn",
        &[
            ("USD", "美元"),
            ("EUR", "欧元"),
            ("GBP", "英镑"),
            ("CNY", "人民币"),
            ("JPY", "日元"),
            ("KRW", "韩元"),
            ("HKD", "港币"),
            ("TWD", "新台币"),
            ("CHF", "瑞士法郎"),
            ("CAD", "加拿大元"),
            ("AUD", "澳大利亚元"),
            ("INR", "印度卢比"),
            ("BHD", "巴林第纳尔"),
            ("KWD", "科威特第纳尔"),
        ],
    ),
    (
        "de-de",
        &[
            ("USD", "US-Dollar"),
            ("EUR", "Euro"),
            ("GBP", "Britisches Pfund"),
            ("CNY", "Chinesischer Yuan"),
            ("JPY", "Japanischer Yen"),
            ("KRW", "Südkoreanischer Won"),
            ("HKD", "Hongkong-Dollar"),
            ("TWD", "Neuer Taiwan-Dollar"),
            ("CHF", "Schweizer Franken"),
            ("CAD", "Kanadischer Dollar"),
            ("AUD", "Australischer Dollar"),
            ("INR", "Indische Rupie"),
            ("BHD", "Bahrainischer Dinar"),
            ("KWD", "Kuwaitischer Dinar"),
        ],
    ),
    (
        "fr-fr",
        &[
            ("USD", "dollar américain"),
            ("EUR", "euro"),
            ("GBP", "livre sterling"),
            ("CNY", "yuan chinois"),
            ("JPY", "yen japonais"),
            ("KRW", "won sud-coréen"),
            ("HKD", "dollar de Hong Kong"),
            ("TWD", "nouveau dollar de Taïwan"),
            ("CHF", "franc suisse"),
            ("CAD", "dollar canadien"),
            ("AUD", "dollar australien"),
            ("INR", "roupie indienne"),
            ("BHD", "dinar bahreïni"),
            ("KWD", "dinar koweïtien"),
        ],
    ),
    (
        "it-it",
        &[
            ("USD", "dollaro statunitense"),
            ("EUR", "euro"),
            ("GBP", "sterlina britannica"),
            ("CNY", "yuan cinese"),
            ("JPY", "yen giapponese"),
            ("KRW", "won sudcoreano"),
            ("HKD", "dollaro di Hong Kong"),
            ("TWD", "nuovo dollaro taiwanese"),
            ("CHF", "franco svizzero"),
            ("CAD", "dollaro canadese"),
            ("AUD", "dollaro australiano"),
            ("INR", "rupia indiana"),
            ("BHD", "dinaro bahreinita"),
            ("KWD", "dinaro kuwaitiano"),
        ],
    ),
    (
        "es-es",
        &[
            ("USD", "dólar estadounidense"),
            ("EUR", "euro"),
            ("GBP", "libra esterlina"),
            ("CNY", "yuan chino"),
            ("JPY", "yen japonés"),
            ("KRW", "won surcoreano"),
            ("HKD", "dólar de Hong Kong"),
            ("TWD", "nuevo dólar taiwanés"),
            ("CHF", "franco suizo"),
            ("CAD", "dólar canadiense"),
            ("AUD", "dólar australiano"),
            ("INR", "rupia india"),
            ("BHD", "dinar bahreiní"),
            ("KWD", "dinar kuwaití"),
        ],
    ),
    (
        "ja-jp",
        &[
            ("USD", "米ドル"),
            ("EUR", "ユーロ"),
            ("GBP", "英ポンド"),
            ("CNY", "中国人民元"),
            ("JPY", "日本円"),
            ("KRW", "韓国ウォン"),
            ("HKD", "香港ドル"),
            ("TWD", "台湾ドル"),
            ("CHF", "スイスフラン"),
            ("CAD", "カナダドル"),
            ("AUD", "オーストラリアドル"),
            ("INR", "インドルピー"),
            ("BHD", "バーレーンディナール"),
            ("KWD", "クウェートディナール"),
        ],
    ),
    (
        "ko-kr",
        &[
            ("USD", "미국 달러"),
            ("EUR", "유로"),
            ("GBP", "영국 파운드"),
            ("CNY", "중국 위안"),
            ("JPY", "일본 엔"),
            ("KRW", "대한민국 원"),
            ("HKD", "홍콩 달러"),
            ("TWD", "신 대만 달러"),
            ("CHF", "스위스 프랑"),
            ("CAD", "캐나다 달러"),
            ("AUD", "호주 달러"),
            ("INR", "인도 루피"),
            ("BHD", "바레인 디나르"),
            ("KWD", "쿠웨이트 디나르"),
        ],
    ),
    (
        "ru-ru",
        &[
            ("USD", "доллар США"),
            ("EUR", "евро"),
            ("GBP", "британский фунт"),
            ("CNY", "китайский юань"),
            ("JPY", "японская иена"),
            ("KRW", "южнокорейская вона"),
            ("HKD", "гонконгский доллар"),
            ("TWD", "новый тайваньский доллар"),
            ("CHF", "швейцарский франк"),
            ("CAD", "канадский доллар"),
            ("AUD", "австралийский доллар"),
            ("INR", "индийская рупия"),
            ("BHD", "бахрейнский динар"),
            ("KWD", "кувейтский динар"),
        ],
    ),
];

fn lookup_currency(currency: &str) -> Option<&'static str> {
    let normalized = currency.trim();
    if normalized.len() != 3 || !normalized.chars().all(|ch| ch.is_ascii_uppercase()) {
        return None;
    }
    CURRENCY_SYMBOLS
        .iter()
        .find(|(code, _)| *code == normalized)
        .map(|(code, _)| *code)
}

fn symbol_for(code: &str) -> &'static str {
    CURRENCY_SYMBOLS
        .iter()
        .find(|(known, _)| *known == code)
        .map(|(_, symbol)| *symbol)
        .unwrap_or("")
}

fn locale_key(locale: &str) -> &'static str {
    let normalized = locale.trim().to_ascii_lowercase();
    let language = normalized.split('-').next().unwrap_or("");
    for (key, _) in LOCALE_RULES {
        let key_language = key.split('-').next().unwrap_or("");
        if *key == normalized || key_language == language {
            return key;
        }
    }
    "en-us"
}

fn resolve_rules(locale: &str) -> &'static LocaleRules {
    let key = locale_key(locale);
    LOCALE_RULES
        .iter()
        .find(|(known, _)| *known == key)
        .map(|(_, rules)| rules)
        .unwrap_or(&LOCALE_RULES[0].1)
}

fn resolve_names(locale: &str) -> &'static [(&'static str, &'static str)] {
    let key = locale_key(locale);
    CURRENCY_NAMES
        .iter()
        .find(|(known, _)| *known == key)
        .map(|(_, names)| *names)
        .unwrap_or(&CURRENCY_NAMES[0].1)
}

fn parse_value(value: f64) -> Option<(bool, bool, String)> {
    if !value.is_finite() {
        return None;
    }
    let negative = value < 0.0;
    let abs = value.abs();
    if abs == 0.0 {
        return Some((false, true, "0".to_string()));
    }
    Some((negative, false, format!("{abs}")))
}

fn split_decimal(abs_decimal: &str) -> (&str, &str) {
    match abs_decimal.find('.') {
        Some(index) => (&abs_decimal[..index], &abs_decimal[index + 1..]),
        None => (abs_decimal, ""),
    }
}

fn increment_decimal(int_part: &str, frac_part: &str) -> (String, String) {
    let mut digits: Vec<u8> = format!("{int_part}{frac_part}").into_bytes();
    let mut index = digits.len();
    while index > 0 {
        index -= 1;
        if digits[index] == b'9' {
            digits[index] = b'0';
        } else {
            digits[index] += 1;
            break;
        }
    }
    if digits.iter().all(|digit| *digit == b'0') {
        digits.insert(0, b'1');
    }
    let cut = digits.len() - frac_part.len();
    let int = String::from_utf8(digits[..cut].to_vec()).unwrap();
    let frac = String::from_utf8(digits[cut..].to_vec()).unwrap();
    (int, frac)
}

fn round_decimal(abs_decimal: &str, max_fraction: u32) -> (String, String) {
    let (int_part, frac_part) = split_decimal(abs_decimal);
    if frac_part.len() <= max_fraction as usize {
        let mut frac = frac_part.to_string();
        while frac.len() < max_fraction as usize {
            frac.push('0');
        }
        return (int_part.to_string(), frac);
    }
    let keep = &frac_part[..max_fraction as usize];
    if frac_part.as_bytes()[max_fraction as usize] >= b'5' {
        return increment_decimal(int_part, keep);
    }
    (int_part.to_string(), keep.to_string())
}

fn trim_fraction(frac_part: &str, min_fraction: u32) -> &str {
    let min = min_fraction as usize;
    let mut end = frac_part.len();
    while end > min && frac_part.as_bytes()[end - 1] == b'0' {
        end -= 1;
    }
    &frac_part[..end]
}

fn group_integer(int_part: &str, grouping: char, use_grouping: bool) -> String {
    if !use_grouping {
        return int_part.to_string();
    }
    let mut grouped = String::new();
    for (index, ch) in int_part.chars().rev().enumerate() {
        if index > 0 && index % 3 == 0 {
            grouped.push(grouping);
        }
        grouped.push(ch);
    }
    grouped.chars().rev().collect()
}

fn shift_decimal_point(abs_decimal: &str, exponent: u32) -> String {
    let (int_part, frac_part) = split_decimal(abs_decimal);
    let digits = format!("{int_part}{frac_part}");
    let point_index = int_part.len() as i64 - exponent as i64;
    if point_index <= 0 {
        return format!("0.{}{}", "0".repeat((-point_index) as usize), digits);
    }
    if point_index as usize >= digits.len() {
        return format!(
            "{}{}",
            digits,
            "0".repeat(point_index as usize - digits.len())
        );
    }
    format!(
        "{}.{}",
        &digits[..point_index as usize],
        &digits[point_index as usize..]
    )
}

fn format_compact_body(parsed: &(bool, bool, String), rules: &LocaleRules) -> String {
    let (_, is_zero, abs_decimal) = parsed;
    if *is_zero {
        return "0".to_string();
    }
    let int_length = split_decimal(abs_decimal).0.len();
    let mut unit_index: Option<usize> = None;
    for (index, unit) in rules.compact.iter().enumerate() {
        if int_length > unit.exponent as usize {
            unit_index = Some(index);
            break;
        }
    }
    let Some(unit_index) = unit_index else {
        let (int_part, frac_part) = round_decimal(abs_decimal, 1);
        let trimmed = trim_fraction(&frac_part, 0);
        return if trimmed.is_empty() {
            int_part
        } else {
            format!("{int_part}.{trimmed}")
        };
    };
    let mut unit = &rules.compact[unit_index];
    let mut scaled = round_decimal(&shift_decimal_point(abs_decimal, unit.exponent), 1);
    if scaled.0.len() > 1 && unit_index + 1 < rules.compact.len() {
        let next_unit = &rules.compact[unit_index + 1];
        scaled = round_decimal(&shift_decimal_point(abs_decimal, next_unit.exponent), 1);
        unit = next_unit;
    }
    let trimmed = trim_fraction(&scaled.1, 0);
    let body = if trimmed.is_empty() {
        scaled.0
    } else {
        format!("{}.{}", scaled.0, trimmed)
    };
    format!("{body}{}", unit.unit)
}

fn sign_prefix(negative: bool, is_zero: bool, sign: &str) -> &'static str {
    match sign {
        "always" => {
            if negative {
                "-"
            } else {
                "+"
            }
        }
        "never" => "",
        "except_zero" => {
            if negative {
                "-"
            } else if is_zero {
                ""
            } else {
                "+"
            }
        }
        _ => {
            if negative {
                "-"
            } else {
                ""
            }
        }
    }
}

fn default_fraction(mode: &str) -> (u32, u32) {
    match mode {
        "compact" => (0, 1),
        "decimal" => (0, 2),
        _ => (2, 2),
    }
}

fn format_money_internal(
    value: f64,
    currency: &str,
    locale: &str,
    mode: &str,
    min_fraction: Option<u32>,
    max_fraction: Option<u32>,
    sign: Option<&str>,
    use_grouping: Option<bool>,
) -> Option<String> {
    let code = lookup_currency(currency)?;
    if !MONEY_MODES.contains(&mode) {
        return None;
    }
    if let Some(sign) = sign {
        if !MONEY_SIGNS.contains(&sign) {
            return None;
        }
    }
    let parsed = parse_value(value)?;

    let (mut min_fraction, mut max_fraction) = match (min_fraction, max_fraction) {
        (None, None) => default_fraction(mode),
        (Some(min), Some(max)) => {
            if min > max || max > 18 {
                return None;
            }
            if mode == "compact" {
                (0, 1)
            } else {
                (min, max)
            }
        }
        _ => return None,
    };
    if mode == "compact" {
        min_fraction = 0;
        max_fraction = 1;
    }

    let use_grouping = use_grouping.unwrap_or(true);
    let sign = sign.unwrap_or("auto");
    let rules = resolve_rules(locale);
    let (negative, is_zero, abs_decimal) = &parsed;
    let symbol = symbol_for(code);

    if mode == "compact" {
        let body = format_compact_body(&parsed, rules);
        let sign_text = sign_prefix(*negative, *is_zero, sign);
        return Some(if rules.prefix {
            format!("{sign_text}{symbol}{body}")
        } else {
            format!("{sign_text}{body} {symbol}")
        });
    }

    let (int_part, frac_part) = round_decimal(abs_decimal, max_fraction);
    let trimmed = trim_fraction(&frac_part, min_fraction);
    let grouped = group_integer(&int_part, rules.grouping, use_grouping);
    let body = if trimmed.is_empty() {
        grouped
    } else {
        format!("{grouped}{}{trimmed}", rules.decimal)
    };
    let sign_text = sign_prefix(*negative, *is_zero, sign);

    if mode == "decimal" {
        return Some(format!("{sign_text}{body}"));
    }

    if mode == "accounting" {
        if *negative && rules.prefix {
            return Some(format!("({symbol}{body})"));
        }
        if *negative && !rules.prefix {
            return Some(format!("-{body} {symbol}"));
        }
        return Some(if rules.prefix {
            format!("{symbol}{body}")
        } else {
            format!("{body} {symbol}")
        });
    }

    if mode == "code" {
        return Some(if rules.prefix {
            format!("{sign_text}{code} {body}")
        } else {
            format!("{sign_text}{body} {code}")
        });
    }

    if mode == "name" {
        let names = resolve_names(locale);
        let name = names
            .iter()
            .find(|(known, _)| *known == code)
            .map(|(_, name)| *name)
            .unwrap_or("US dollars");
        let separator = if rules.name_space { " " } else { "" };
        return Some(format!("{sign_text}{body}{separator}{name}"));
    }

    Some(if rules.prefix {
        format!("{sign_text}{symbol}{body}")
    } else {
        format!("{sign_text}{body} {symbol}")
    })
}

pub fn money_symbol(currency: &str) -> Option<String> {
    let code = lookup_currency(currency)?;
    Some(symbol_for(code).to_string())
}

pub fn format_money(value: f64, currency: &str, locale: &str, mode: &str) -> Option<String> {
    format_money_internal(value, currency, locale, mode, None, None, None, None)
}

pub fn format_money_digits(
    value: f64,
    currency: &str,
    locale: &str,
    mode: &str,
    min_fraction: u32,
    max_fraction: u32,
) -> Option<String> {
    format_money_internal(
        value,
        currency,
        locale,
        mode,
        Some(min_fraction),
        Some(max_fraction),
        None,
        None,
    )
}

pub fn format_money_minor_units(
    minor: i64,
    currency: &str,
    locale: &str,
    mode: &str,
) -> Option<String> {
    if mode == "compact" || !MONEY_MODES.contains(&mode) {
        return None;
    }
    let exponent = minor_unit_exponent(currency)?;
    let major = minor as f64 / 10_f64.powi(exponent as i32);
    format_money_internal(
        major,
        currency,
        locale,
        mode,
        Some(exponent),
        Some(exponent),
        None,
        None,
    )
}

pub fn format_money_options(
    value: f64,
    currency: &str,
    locale: &str,
    mode: &str,
    min_fraction: u32,
    max_fraction: u32,
    sign: &str,
    use_grouping: bool,
) -> Option<String> {
    format_money_internal(
        value,
        currency,
        locale,
        mode,
        Some(min_fraction),
        Some(max_fraction),
        Some(sign),
        Some(use_grouping),
    )
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn money_helpers() {
        assert_eq!(money_symbol("CNY"), Some("¥".to_string()));
        assert_eq!(money_symbol("xyz"), None);
        assert_eq!(
            format_money(1234.5, "USD", "en-US", "symbol"),
            Some("$1,234.50".to_string())
        );
        assert_eq!(
            format_money(1234.5, "CNY", "zh-CN", "name"),
            Some("1,234.50人民币".to_string())
        );
        assert_eq!(
            format_money(-1234.5, "USD", "en-US", "accounting"),
            Some("($1,234.50)".to_string())
        );
        assert_eq!(
            format_money(12000.0, "CNY", "zh-CN", "compact"),
            Some("¥1.2万".to_string())
        );
        assert_eq!(
            format_money_digits(0.12345, "USD", "en-US", "symbol", 2, 4),
            Some("$0.1235".to_string())
        );
        assert_eq!(
            format_money_minor_units(123450, "USD", "en-US", "symbol"),
            Some("$1,234.50".to_string())
        );
        assert_eq!(
            format_money_options(1234.5, "USD", "en-US", "symbol", 2, 2, "always", true),
            Some("+$1,234.50".to_string())
        );
        assert_eq!(format_money(1234.5, "XYZ", "en-US", "symbol"), None);
        assert_eq!(format_money(1234.5, "USD", "en-US", "invalid"), None);
    }
}
