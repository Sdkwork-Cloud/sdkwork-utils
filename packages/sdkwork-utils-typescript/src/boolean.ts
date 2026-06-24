import { isBlank } from "./string.js";

export function parseBool(value: string): boolean | null {
  switch (value.trim().toLowerCase()) {
    case "true":
    case "1":
    case "yes":
    case "on":
      return true;
    case "false":
    case "0":
    case "no":
    case "off":
      return false;
    default:
      return null;
  }
}

export function isTruthy(value: string | null | undefined): boolean {
  if (isBlank(value)) {
    return false;
  }
  switch (value!.trim().toLowerCase()) {
    case "false":
    case "0":
    case "no":
    case "off":
      return false;
    default:
      return true;
  }
}
