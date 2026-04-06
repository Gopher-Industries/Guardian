import ReactDataTable from 'react-data-table-component';

function TableLoader() {
  return (
    <div className='table-loader'>
      <div className='table-loader-spinner' />
      <span>Loading...</span>
    </div>
  );
}

export default function DataTable({
  columns,
  data,
  loading = false,
  totalRows = 0,
  onChangePage,
  perPage = 10,
  perPageOptions = [10, 20],
  loadingMessage,
  ...rest
}) {
  const progressComponent = loadingMessage ? (
    <div className='table-loader'>
      <div className='table-loader-spinner' />
      <span>{loadingMessage}</span>
    </div>
  ) : (
    <TableLoader />
  );

  return (
    <ReactDataTable
      columns={columns}
      data={data}
      progressPending={loading}
      progressComponent={progressComponent}
      pagination
      paginationServer
      paginationTotalRows={totalRows}
      paginationPerPage={perPage}
      paginationRowsPerPageOptions={perPageOptions}
      onChangePage={onChangePage}
      {...rest}
    />
  );
}
