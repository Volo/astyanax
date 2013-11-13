package com.netflix.astyanax.cql.reads;

import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.google.common.util.concurrent.ListenableFuture;
import com.netflix.astyanax.CassandraOperationType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.cql.CqlAbstractExecutionImpl;
import com.netflix.astyanax.cql.CqlKeyspaceImpl.KeyspaceContext;
import com.netflix.astyanax.cql.writes.CqlColumnListMutationImpl.ColumnFamilyMutationContext;
import com.netflix.astyanax.query.ColumnCountQuery;

public class CqlColumnCountQueryImpl implements ColumnCountQuery {

	private final KeyspaceContext ksContext;
	private final ColumnFamilyMutationContext<?, ?> cfContext;
	private final Statement query;
	
	public CqlColumnCountQueryImpl(KeyspaceContext ksCtx, ColumnFamilyMutationContext<?,?> cfCtx, Statement query) {
		this.ksContext = ksCtx;
		this.cfContext = cfCtx;
		this.query = query;
	}
	
	@Override
	public OperationResult<Integer> execute() throws ConnectionException {
		return new InternalColumnCountExecutionImpl(query).execute();
	}

	@Override
	public ListenableFuture<OperationResult<Integer>> executeAsync() throws ConnectionException {
		return new InternalColumnCountExecutionImpl(query).executeAsync();
	}

	private class InternalColumnCountExecutionImpl extends CqlAbstractExecutionImpl<Integer> {

		public InternalColumnCountExecutionImpl(Statement query) {
			super(ksContext, cfContext);
		}

		@Override
		public CassandraOperationType getOperationType() {
			return CassandraOperationType.GET_COLUMN_COUNT;
		}

		@Override
		public Statement getQuery() {
			return query;
		}

		@Override
		public Integer parseResultSet(ResultSet resultSet) {
			List<Row> rows = resultSet.all();
			if (rows != null) {
				return rows.size();
			} else {
				return 0;
			}
		}
	}
}