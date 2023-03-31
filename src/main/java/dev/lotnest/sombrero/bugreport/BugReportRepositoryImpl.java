package dev.lotnest.sombrero.bugreport;

import dev.lotnest.sombrero.crud.BaseCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository("bugReportRepository")
public interface BugReportRepositoryImpl extends BaseCrudRepositoryImpl<BugReport, Long>, BugReportRepository {
}
