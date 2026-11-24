import { useEffect, useState } from 'react'
import { api } from '../api'

const VAZIO = { nome: '', email: '', senha: '', cpf: '', rg: '', endereco: '', curso: '', instituicaoId: '' }

/** Cadastro (sem `aluno`) ou edição (com `aluno` e `token`). */
export default function AlunoForm({ aluno, token, onSalvo }) {
  const [form, setForm] = useState(aluno ? { ...aluno, senha: '' } : VAZIO)
  const [instituicoes, setInstituicoes] = useState([])
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState('')

  useEffect(() => {
    api('/api/instituicoes').then(setInstituicoes).catch(e => setErro(e.message))
  }, [])

  const campo = nome => e => setForm({ ...form, [nome]: e.target.value })

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    setOk('')
    try {
      const body = { ...form, instituicaoId: Number(form.instituicaoId) }
      const salvo = aluno
        ? await api(`/api/alunos/${aluno.id}`, { method: 'PUT', body, token })
        : await api('/api/alunos', { method: 'POST', body })
      if (aluno) setOk('Dados atualizados.')
      onSalvo?.(salvo)
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <form onSubmit={enviar}>
      <label>Nome<input value={form.nome} onChange={campo('nome')} required /></label>
      <label>E-mail<input type="email" value={form.email} onChange={campo('email')} required /></label>
      <label>
        Senha{aluno && ' (deixe vazio para manter)'}
        <input type="password" value={form.senha} onChange={campo('senha')} minLength={6} required={!aluno} />
      </label>
      <div className="grade2">
        <label>CPF<input value={form.cpf} onChange={campo('cpf')} required /></label>
        <label>RG<input value={form.rg} onChange={campo('rg')} required /></label>
      </div>
      <label>Endereço<input value={form.endereco} onChange={campo('endereco')} required /></label>
      <div className="grade2">
        <label>
          Instituição
          <select value={form.instituicaoId} onChange={campo('instituicaoId')} required>
            <option value="">Selecione…</option>
            {instituicoes.map(i => <option key={i.id} value={i.id}>{i.nome}</option>)}
          </select>
        </label>
        <label>Curso<input value={form.curso} onChange={campo('curso')} required /></label>
      </div>
      {erro && <p className="erro">{erro}</p>}
      {ok && <p className="ok">{ok}</p>}
      <button type="submit">{aluno ? 'Salvar alterações' : 'Cadastrar'}</button>
    </form>
  )
}
