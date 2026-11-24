import { useState } from 'react'
import { api } from '../api'

export default function Login({ onEntrar, onIr, aviso }) {
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    try {
      onEntrar(await api('/api/auth/login', { method: 'POST', body: { email, senha } }))
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <form className="card" onSubmit={enviar}>
      <h2>Entrar</h2>
      {aviso && <p className="ok">{aviso}</p>}
      <label>E-mail<input type="email" value={email} onChange={e => setEmail(e.target.value)} required /></label>
      <label>Senha<input type="password" value={senha} onChange={e => setSenha(e.target.value)} required /></label>
      {erro && <p className="erro">{erro}</p>}
      <button type="submit">Entrar</button>
      <p className="rodape">
        Não tem conta?{' '}
        <button type="button" className="link" onClick={() => onIr('cadastroAluno')}>Sou aluno</button> ·{' '}
        <button type="button" className="link" onClick={() => onIr('cadastroEmpresa')}>Sou empresa parceira</button>
      </p>
    </form>
  )
}
