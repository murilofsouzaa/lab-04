import { useState } from 'react'
import { api } from '../api'

const VAZIO = { nome: '', email: '', senha: '', cnpj: '' }

/** Cadastro (sem `empresa`) ou edição (com `empresa` e `token`). */
export default function EmpresaForm({ empresa, token, onSalvo }) {
  const [form, setForm] = useState(empresa ? { ...empresa, senha: '' } : VAZIO)
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState('')

  const campo = nome => e => setForm({ ...form, [nome]: e.target.value })

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    setOk('')
    try {
      const salvo = empresa
        ? await api(`/api/empresas/${empresa.id}`, { method: 'PUT', body: form, token })
        : await api('/api/empresas', { method: 'POST', body: form })
      if (empresa) setOk('Dados atualizados.')
      onSalvo?.(salvo)
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <form onSubmit={enviar}>
      <label>Nome da empresa<input value={form.nome} onChange={campo('nome')} required /></label>
      <label>CNPJ<input value={form.cnpj} onChange={campo('cnpj')} required /></label>
      <label>E-mail<input type="email" value={form.email} onChange={campo('email')} required /></label>
      <label>
        Senha{empresa && ' (deixe vazio para manter)'}
        <input type="password" value={form.senha} onChange={campo('senha')} minLength={6} required={!empresa} />
      </label>
      {erro && <p className="erro">{erro}</p>}
      {ok && <p className="ok">{ok}</p>}
      <button type="submit">{empresa ? 'Salvar alterações' : 'Cadastrar'}</button>
    </form>
  )
}
