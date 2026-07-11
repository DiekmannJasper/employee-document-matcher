import { useQuery } from "@tanstack/react-query";
import { fetchDocumentCategories } from "../api/documentCategoryApi";

export function useDocumentCategories() {
  return useQuery({
    queryKey: ["document-categories"],
    queryFn: fetchDocumentCategories,
  });
}
