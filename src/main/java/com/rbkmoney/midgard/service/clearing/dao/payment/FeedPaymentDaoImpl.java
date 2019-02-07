package com.rbkmoney.midgard.service.clearing.dao.payment;

import com.rbkmoney.midgard.service.clearing.dao.common.AbstractGenericDao;
import com.rbkmoney.midgard.service.clearing.dao.common.RecordRowMapper;
import com.rbkmoney.midgard.service.clearing.exception.DaoException;
import org.jooq.Query;
import org.jooq.generated.feed.enums.PaymentStatus;
import org.jooq.generated.feed.tables.pojos.CashFlow;
import org.jooq.generated.feed.tables.pojos.Payment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;

import static org.jooq.generated.feed.tables.CashFlow.CASH_FLOW;
import static org.jooq.generated.feed.tables.Payment.PAYMENT;

@Component
public class FeedPaymentDaoImpl extends AbstractGenericDao implements PaymentDao {

    private final RowMapper<Payment> paymentRowMapper;

    private final RowMapper<CashFlow> cashFlowRowMapper;

    public FeedPaymentDaoImpl(DataSource dataSource) {
        super(dataSource);
        paymentRowMapper = new RecordRowMapper<>(PAYMENT, Payment.class);
        cashFlowRowMapper = new RecordRowMapper<>(CASH_FLOW, CashFlow.class);
    }

    @Override
    public List<Payment> getPayments(long eventId, List<Integer> providerIds, int poolSize) throws DaoException {
        Query query = getDslContext().selectFrom(PAYMENT)
                .where(PAYMENT.EVENT_ID.greaterThan(eventId))
                .and(PAYMENT.STATUS.in(PaymentStatus.captured, PaymentStatus.cancelled))
                .and(PAYMENT.ROUTE_PROVIDER_ID.in(providerIds))
                .orderBy(PAYMENT.EVENT_ID)
                .limit(poolSize);
        return fetch(query, paymentRowMapper);
    }

    @Override
    public List<CashFlow> getCashFlow(long objId) throws DaoException {
        Query query = getDslContext().selectFrom(CASH_FLOW)
                .where(CASH_FLOW.OBJ_ID.eq(objId));
        return fetch(query, cashFlowRowMapper);
    }

}
