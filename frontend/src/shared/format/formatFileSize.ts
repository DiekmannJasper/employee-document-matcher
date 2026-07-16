import { de } from "../i18n/de";

const UNITS = de.fileSize.units;

export function formatFileSize(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} ${de.fileSize.bytes}`;
  }

  let value = bytes / 1024;
  let unitIndex = 0;
  while (value >= 1024 && unitIndex < UNITS.length - 1) {
    value /= 1024;
    unitIndex += 1;
  }

  return `${value.toFixed(1)} ${UNITS[unitIndex]}`;
}
