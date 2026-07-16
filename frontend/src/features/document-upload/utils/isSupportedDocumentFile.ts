const SUPPORTED_EXTENSIONS = [".pdf", ".docx", ".xml"];

const SUPPORTED_CONTENT_TYPES = [
  "application/pdf",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "application/xml",
  "text/xml",
];

// Extension is checked in addition to content-type since browsers/OS file pickers are
// inconsistent about what content-type they report for less common formats like .docx or .xml.
// The backend is the actual source of truth (magic-byte detection); this is just early UX feedback.
export function isSupportedDocumentFile(file: File): boolean {
  const name = file.name.toLowerCase();
  return SUPPORTED_CONTENT_TYPES.includes(file.type) || SUPPORTED_EXTENSIONS.some((extension) => name.endsWith(extension));
}
