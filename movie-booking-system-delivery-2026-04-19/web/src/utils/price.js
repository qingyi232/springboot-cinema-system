function parsePositiveNumber(value) {
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue) || numericValue <= 0) {
    return null;
  }
  return numericValue;
}

export function canBuyTicket(value) {
  return parsePositiveNumber(value) !== null;
}

export function formatTicketPrice(value, options = {}) {
  const {
    fallback = "待排片",
    suffix = "起",
    digits = 0
  } = options;
  const numericValue = parsePositiveNumber(value);
  if (numericValue === null) {
    return fallback;
  }
  return `￥${numericValue.toFixed(digits)}${suffix}`;
}

export function formatSingleTicketPrice(value, options = {}) {
  return formatTicketPrice(value, {
    fallback: options.fallback || "待排片",
    suffix: "/张",
    digits: options.digits ?? 0
  });
}

export function formatOrderAmount(value, options = {}) {
  const {
    fallback = "0",
    digits = 0
  } = options;
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue) || numericValue < 0) {
    return fallback;
  }
  return numericValue.toFixed(digits);
}

export function formatBoxOffice(value) {
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue) || numericValue <= 0) {
    return "暂无数据";
  }
  if (numericValue >= 100000000) {
    return `${(numericValue / 100000000).toFixed(1)}亿`;
  }
  if (numericValue >= 10000) {
    return `${(numericValue / 10000).toFixed(1)}万`;
  }
  return `${numericValue.toFixed(0)}元`;
}
