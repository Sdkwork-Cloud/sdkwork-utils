const EMAIL_RE = /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/;
const UUID_RE =
  /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/;
const URL_RE = /^https?:\/\/[^\s/$.?#].[^\s]*$/;
const IPV4_RE =
  /^(25[0-5]|2[0-4]\d|1?\d?\d)(\.(25[0-5]|2[0-4]\d|1?\d?\d)){3}$/;
const E164_RE = /^\+[1-9]\d{1,14}$/;

function isIpv6Shape(value: string): boolean {
  if (!value || !/^[0-9a-fA-F:]+$/.test(value)) {
    return false;
  }
  if ((value.match(/::/g) ?? []).length > 1) {
    return false;
  }
  const isValidPart = (part: string) => part.length > 0 && part.length <= 4;
  if (value.includes("::")) {
    const [left, right] = value.split("::");
    const leftParts = left ? left.split(":").filter(Boolean) : [];
    const rightParts = right ? right.split(":").filter(Boolean) : [];
    if (!leftParts.every(isValidPart) || !rightParts.every(isValidPart)) {
      return false;
    }
    return leftParts.length + rightParts.length < 8;
  }
  const parts = value.split(":");
  return parts.length === 8 && parts.every(isValidPart);
}

export function isEmail(value: string): boolean {
  return EMAIL_RE.test(value.trim());
}

export function isUuid(value: string): boolean {
  return UUID_RE.test(value.trim());
}

export function isUrl(value: string): boolean {
  return URL_RE.test(value.trim());
}

export function isNumeric(value: string): boolean {
  return Number.isFinite(Number.parseFloat(value.trim()));
}

export function isIpv4(value: string): boolean {
  return IPV4_RE.test(value.trim());
}

export function isIpv6(value: string): boolean {
  return isIpv6Shape(value.trim());
}

export function isPhoneE164(value: string): boolean {
  return E164_RE.test(value.trim());
}
