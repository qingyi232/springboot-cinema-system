import dayjs from 'dayjs';
import { canBuyTicket } from '@/utils/price.js';

export function getMovieReleaseDate(movie) {
  const releaseDate = movie?.releaseDate;
  if (!releaseDate) {
    return null;
  }
  const parsed = dayjs(releaseDate);
  return parsed.isValid() ? parsed : null;
}

export function isUpcomingMovie(movie) {
  const releaseDate = getMovieReleaseDate(movie);
  return !!(releaseDate && releaseDate.isAfter(dayjs(), 'day'));
}

export function isNowShowingMovie(movie) {
  return !isUpcomingMovie(movie) && canBuyTicket(movie?.lowestPrice);
}

export function isUnavailableMovie(movie) {
  return !isUpcomingMovie(movie) && !isNowShowingMovie(movie);
}

export function getMovieAvailabilityState(movie) {
  if (isNowShowingMovie(movie)) {
    return 'buyable';
  }
  if (isUpcomingMovie(movie)) {
    return 'upcoming';
  }
  return 'unavailable';
}

export function sortByUpcomingRelease(a, b) {
  const aDate = getMovieReleaseDate(a);
  const bDate = getMovieReleaseDate(b);
  if (!aDate && !bDate) return 0;
  if (!aDate) return 1;
  if (!bDate) return -1;
  return aDate.valueOf() - bDate.valueOf();
}

export function sortByHotWeight(a, b) {
  const boxOfficeDiff = Number(b?.boxOffice || 0) - Number(a?.boxOffice || 0);
  if (boxOfficeDiff !== 0) {
    return boxOfficeDiff;
  }
  return Number(b?.id || 0) - Number(a?.id || 0);
}
