const ConnectionTypeToggle = ({
  type,
  onChange
}: {
  type: 'detailed' | 'url'
  onChange: (type: 'detailed' | 'url') => void
}) => {
  return (
    <div className='border-b'>
      <div className='flex gap-4'>
        <button
          onClick={() => onChange('detailed')}
          className={`pb-3 px-4 font-medium text-sm ${
            type === 'detailed'
              ? 'border-b-2 border-primary text-primary'
              : 'text-muted-foreground'
          }`}
          type='button'
        >
          Formulario Detallado
        </button>
        <button
          onClick={() => onChange('url')}
          className={`pb-3 px-4 font-medium text-sm ${
            type === 'url'
              ? 'border-b-2 border-primary text-primary'
              : 'text-muted-foreground'
          }`}
          type='button'
        >
          URL de Conexión
        </button>
      </div>
    </div>
  )
}

export default ConnectionTypeToggle
