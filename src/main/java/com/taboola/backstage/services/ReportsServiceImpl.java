package com.taboola.backstage.services;

import com.taboola.backstage.exceptions.BackstageAPIConnectivityException;
import com.taboola.backstage.exceptions.BackstageAPIRequestException;
import com.taboola.backstage.exceptions.BackstageAPIUnauthorizedException;
import com.taboola.backstage.internal.BackstagePublisherReportsEndpoint;
import com.taboola.backstage.model.ColumnsMetadata;
import com.taboola.backstage.model.Report;
import com.taboola.backstage.model.ReportFilter;
import com.taboola.backstage.model.auth.BackstageAuthentication;
import com.taboola.backstage.model.dynamic.DynamicField;
import com.taboola.backstage.model.dynamic.DynamicFieldMetadata;
import com.taboola.backstage.model.dynamic.DynamicFields;
import com.taboola.backstage.model.dynamic.DynamicRow;
import com.taboola.backstage.model.media.reports.*;
import com.taboola.backstage.internal.BackstageMediaReportsEndpoint;
import com.taboola.backstage.model.publishers.reports.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vladi
 * Date: 10/28/2017
 * Time: 11:10 PM
 * By Taboola
 */
public class ReportsServiceImpl implements ReportsService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final BackstageMediaReportsEndpoint mediaReportsEndpoint;
    private final BackstagePublisherReportsEndpoint publisherReportsService;
    private final Boolean organizeDynamicColumns;

    public ReportsServiceImpl(BackstageMediaReportsEndpoint mediaReportsEndpoint, BackstagePublisherReportsEndpoint publisherReportsService, Boolean organizeDynamicColumns) {
        this.mediaReportsEndpoint = mediaReportsEndpoint;
        this.publisherReportsService = publisherReportsService;
        this.organizeDynamicColumns = organizeDynamicColumns;
    }

    @Override
    public TopCampaignContentReport getTopCampaignContentReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return getTopCampaignContentReport(auth, accountId, startDate, endDate, Collections.emptyMap());
    }

    @Override
    public TopCampaignContentReport getTopCampaignContentReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                                Map<TopCampaignContentOptionalFilters, String> filters) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        String accessToken = auth.getToken().getAccessTokenForHeader();
        TopCampaignContentReport report = mediaReportsEndpoint.getTopCampaignContentReport(accessToken, accountId,
                DATE_TIME_FORMATTER.format(startDate),
                DATE_TIME_FORMATTER.format(endDate),
                formatOptionalFilters(filters));

        tryOrganizingDynamicColumns(report);
        return report;
    }

    @Override
    public CampaignSummaryReport getCampaignSummaryReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate, CampaignSummaryDimensions dimension) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return getCampaignSummaryReport(auth, accountId, startDate, endDate, dimension, Collections.emptyMap());
    }

    @Override
    public CampaignSummaryReport getCampaignSummaryReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                          CampaignSummaryDimensions dimension, Map<CampaignSummaryOptionalFilters, String> filters) {
        String accessToken = auth.getToken().getAccessTokenForHeader();
        //TODO verify correct use of filters based on spec and save HTTP call
        CampaignSummaryReport report = mediaReportsEndpoint.getCampaignSummary(accessToken, accountId, dimension.getName(),
                DATE_TIME_FORMATTER.format(startDate), DATE_TIME_FORMATTER.format(endDate),
                formatOptionalFilters(filters));

        tryOrganizingDynamicColumns(report);
        return report;
    }

    private <T extends ReportFilter> Map<String, String> formatOptionalFilters(Map<T, String> filters) {
        return filters.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));
    }

    @Override
    public RevenueSummaryReport getRevenueSummaryReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                        RevenueSummaryDimensions dimension) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return getRevenueSummaryReport(auth, accountId, startDate, endDate, dimension, Collections.emptyMap());
    }

    @Override
    public RevenueSummaryReport getRevenueSummaryReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                        RevenueSummaryDimensions dimension, Map<RevenueSummaryOptionalFilters, String> filters) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        String accessToken = auth.getToken().getAccessTokenForHeader();
        //TODO verify correct use of filters based on spec and save HTTP call
        return publisherReportsService.getRevenueSummary(accessToken, accountId, dimension.getName(),
                                                        DATE_TIME_FORMATTER.format(startDate),
                                                        DATE_TIME_FORMATTER.format(endDate),
                                                        formatOptionalFilters(filters));
    }

    @Override
    public VisitValueReport getVisitValueReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                VisitValueDimensions dimension) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return getVisitValueReport(auth, accountId, startDate, endDate, dimension, Collections.emptyMap());
    }

    @Override
    public VisitValueReport getVisitValueReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                VisitValueDimensions dimension, Map<VisitValueOptionalFilters, String> filters) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        String accessToken = auth.getToken().getAccessTokenForHeader();
        //TODO verify correct use of filters based on spec and save HTTP call
        return publisherReportsService.getVisitValue(accessToken, accountId, dimension.getName(),
                                                     DATE_TIME_FORMATTER.format(startDate),
                                                     DATE_TIME_FORMATTER.format(endDate),
                                                     formatOptionalFilters(filters));
    }

    @Override
    public RecirculationSummaryReport getRecirculationSummaryReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                                    RecirculationSummaryDimensions dimension) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return getRecirculationSummaryReport(auth, accountId, startDate, endDate, dimension, Collections.emptyMap());
    }

    @Override
    public RecirculationSummaryReport getRecirculationSummaryReport(BackstageAuthentication auth, String accountId, LocalDate startDate, LocalDate endDate,
                                                                    RecirculationSummaryDimensions dimension, Map<RecirculationSummaryOptionalFilters, String> filters) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        String accessToken = auth.getToken().getAccessTokenForHeader();
        //TODO verify correct use of filters based on spec and save HTTP call
        return publisherReportsService.getRecirculationSummary(accessToken, accountId, dimension.getName(),
                                                               DATE_TIME_FORMATTER.format(startDate),
                                                               DATE_TIME_FORMATTER.format(endDate),
                                                               formatOptionalFilters(filters));
    }

    protected <R extends DynamicRow> void tryOrganizingDynamicColumns(Report<R> report) {
        if(!organizeDynamicColumns) {
            return;
        }

        ColumnsMetadata metadata = report.getMetadata();
        if(metadata == null || metadata.getDynamicFields() == null) {
            return;
        }

        Map<String, DynamicFieldMetadata> dynamicFieldIdToMetadata = metadata.getDynamicFields()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(DynamicFieldMetadata::getId, v -> v));

        Collection<R> rows = report.getResults();
        for(R row : rows) {
            DynamicFields dynamicFields = row.getDynamicFields();
            for(DynamicField field : dynamicFields) {
                DynamicFieldMetadata dynamicFieldMetadata = dynamicFieldIdToMetadata.get(field.getId());
                field.setDynamicFieldMetadata(dynamicFieldMetadata);
            }
        }

    }
}
