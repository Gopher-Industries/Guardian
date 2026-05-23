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

  const isDarkMode = document.body.classList.contains('dark-theme');

  const customStyles = {
    table: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--text)',
      },
    },
    tableWrapper: {
      style: {
        backgroundColor: 'var(--surface)',
      },
    },
    responsiveWrapper: {
      style: {
        backgroundColor: 'var(--surface)',
        borderRadius: '18px',
      },
    },
    header: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--primary-dark)',
        minHeight: '0',
        paddingLeft: 0,
        paddingRight: 0,
      },
    },
    subHeader: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--text)',
      },
    },
    headRow: {
      style: {
        backgroundColor: 'var(--surface-soft)',
        borderTopStyle: 'solid',
        borderTopWidth: '1px',
        borderTopColor: 'var(--border)',
        borderBottomStyle: 'solid',
        borderBottomWidth: '1px',
        borderBottomColor: 'var(--border)',
        minHeight: '52px',
      },
    },
    headCells: {
      style: {
        backgroundColor: 'var(--surface-soft)',
        color: 'var(--primary-dark)',
        fontSize: '14px',
        fontWeight: 700,
      },
    },
    rows: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--text)',
        minHeight: '58px',
        borderBottomStyle: 'solid',
        borderBottomWidth: '1px',
        borderBottomColor: 'var(--border)',
      },
      highlightOnHoverStyle: {
        backgroundColor: 'var(--surface-soft)',
        color: 'var(--text)',
        transitionDuration: '0.15s',
        transitionProperty: 'background-color',
        outline: 'none',
      },
    },
    cells: {
      style: {
        color: 'var(--text)',
        fontSize: '14px',
      },
    },
    pagination: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--text-muted)',
        borderTopStyle: 'solid',
        borderTopWidth: '1px',
        borderTopColor: 'var(--border)',
        minHeight: '56px',
      },
      pageButtonsStyle: {
        borderRadius: '8px',
        height: '32px',
        width: '32px',
        padding: 0,
        margin: '0 2px',
        cursor: 'pointer',
        transition: '0.2s ease',
        color: isDarkMode ? 'var(--text-muted)' : '#6b7280',
        fill: isDarkMode ? 'var(--text-muted)' : '#6b7280',
        backgroundColor: 'transparent',
        '&:disabled': {
          cursor: 'not-allowed',
          color: '#94a3b8',
          fill: '#94a3b8',
        },
        '&:hover:not(:disabled)': {
          backgroundColor: 'var(--surface-soft)',
        },
        '&:focus': {
          outline: 'none',
        },
      },
    },
    noData: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--text-muted)',
        padding: '24px',
      },
    },
    progress: {
      style: {
        backgroundColor: 'var(--surface)',
        color: 'var(--text)',
      },
    },
  };

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
      customStyles={customStyles}
      {...rest}
    />
  );
}