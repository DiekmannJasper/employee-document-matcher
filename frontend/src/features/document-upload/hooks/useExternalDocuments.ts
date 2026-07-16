import { useQuery } from "@tanstack/react-query";
import { fetchExternalDocuments } from "../api/externalDocumentApi";

export function useExternalDocuments() {
  return useQuery({
    queryKey: ["external-documents"],
    queryFn: fetchExternalDocuments,
  });
}
