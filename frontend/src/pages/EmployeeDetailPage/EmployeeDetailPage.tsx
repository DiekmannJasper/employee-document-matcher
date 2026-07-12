import { useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { useDocumentCategories } from "../../features/document-categories/hooks/useDocumentCategories";
import type { CategoryFolder } from "../../features/employee-documents/components/CategoryFolderTabs";
import { CategoryFolderTabs } from "../../features/employee-documents/components/CategoryFolderTabs";
import { DocumentList } from "../../features/employee-documents/components/DocumentList";
import type { DocumentSummary } from "../../features/employee-documents/api/employeeDocumentApi";
import { useEmployeeDocuments } from "../../features/employee-documents/hooks/useEmployeeDocuments";
import { EmployeeMasterData } from "../../features/employees/components/EmployeeMasterData";
import { useEmployee } from "../../features/employees/hooks/useEmployee";
import { EmptyState } from "../../shared/components/EmptyState/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState/LoadingState";
import { PageContainer } from "../../shared/components/PageContainer/PageContainer";
import { de } from "../../shared/i18n/de";

const ALL_FOLDER_ID = "all";
const UNASSIGNED_FOLDER_ID = "unassigned";

export function EmployeeDetailPage() {
  const { employeeId } = useParams<{ employeeId: string }>();
  const [selectedFolderId, setSelectedFolderId] = useState<string>(ALL_FOLDER_ID);

  const employeeQuery = useEmployee(employeeId);
  const categoriesQuery = useDocumentCategories();
  const documentsQuery = useEmployeeDocuments(employeeId);

  const documentsByCategory = useMemo(() => {
    const grouped = new Map<string, DocumentSummary[]>();
    for (const document of documentsQuery.data ?? []) {
      const key = document.categoryId ?? UNASSIGNED_FOLDER_ID;
      const existing = grouped.get(key) ?? [];
      existing.push(document);
      grouped.set(key, existing);
    }
    return grouped;
  }, [documentsQuery.data]);

  const folders = useMemo<CategoryFolder[]>(() => {
    const totalCount = documentsQuery.data?.length ?? 0;
    const categoryFolders = (categoriesQuery.data ?? []).map((category) => ({
      id: category.id,
      label: category.displayName,
      count: documentsByCategory.get(category.id)?.length ?? 0,
    }));

    return [
      { id: ALL_FOLDER_ID, label: de.documents.all, count: totalCount },
      ...categoryFolders,
      { id: UNASSIGNED_FOLDER_ID, label: de.documents.unassigned, count: documentsByCategory.get(UNASSIGNED_FOLDER_ID)?.length ?? 0 },
    ];
  }, [categoriesQuery.data, documentsByCategory, documentsQuery.data]);

  const visibleDocuments = useMemo(() => {
    if (selectedFolderId === ALL_FOLDER_ID) {
      return documentsQuery.data ?? [];
    }
    return documentsByCategory.get(selectedFolderId) ?? [];
  }, [selectedFolderId, documentsByCategory, documentsQuery.data]);

  const isPending = employeeQuery.isPending || categoriesQuery.isPending || documentsQuery.isPending;
  const isError = employeeQuery.isError || categoriesQuery.isError || documentsQuery.isError;

  return (
    <PageContainer>
      {isPending && <LoadingState message={de.documents.recordLoading} />}
      {isError && (
        <ErrorState
          message={de.documents.recordLoadError}
          onRetry={() => {
            employeeQuery.refetch();
            categoriesQuery.refetch();
            documentsQuery.refetch();
          }}
        />
      )}
      {!isPending && !isError && employeeQuery.data && (
        <>
          <EmployeeMasterData employee={employeeQuery.data} />
          <CategoryFolderTabs folders={folders} selectedId={selectedFolderId} onSelect={setSelectedFolderId} />
          {visibleDocuments.length === 0 ? (
            <EmptyState message={de.documents.categoryEmpty} />
          ) : (
            <DocumentList documents={visibleDocuments} />
          )}
        </>
      )}
    </PageContainer>
  );
}
