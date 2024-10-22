import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const toTitleCase = (str: string | null | undefined) => {
  if (typeof str !== "string") return ""; // Return an empty string if null, undefined, or not a string

  return str
    .toLocaleLowerCase()
    .replace(/_/g, " ") // Replace underscores with spaces
    .replace(/\b\w/g, (char) => char.toUpperCase()); // Convert to title case
};

export const getLastWord = (str: string | null | undefined) => {
  if (typeof str !== "string") return ""; // Handle null, undefined, or non-string input

  const words = str.trim().split(/\s+/); // Split by spaces and remove extra spaces
  return words.length > 0 ? words[words.length - 1] : ""; // Return the last word, or an empty string if no words
};

export const formatReadableDate = (dateString: string | null | undefined) => {
  if (!dateString) return ""; // Handle null, undefined, or empty input

  const date = new Date(dateString);
  if (isNaN(date.getTime())) return ""; // Handle invalid dates

  const options: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "long",
    day: "numeric",
  };
  return date.toLocaleDateString(undefined, options); // Return the date in a readable format
};
