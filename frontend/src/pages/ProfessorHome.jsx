import { useEffect, useState } from 'react'
import { api } from '../api'
import Abas from '../components/Abas'
import Extrato from '../components/Extrato'

export default function ProfessorHome({ sessao }) {
  const [aba, setAba] = useState('enviar')
  const [alunos, setAlunos] = useState([])
  const [saldo, setSaldo] = useState(null)
  const [form, setForm] = useState({ idAluno: '', valor: '', motivo: '' })
  const [msg, setMsg] = useState(null)
  const [versao, setVersao] = useState(0)
  const { token, id } = sessao

  useEffect(() => {
    api('/api/alunos', { token }).then(setAlunos).catch(e => setMsg({ erro: e.message }))
  }, [token])

  useEffect(() => {
    api(`/api/professores/${id}/extrato`, { token }).then(e => setSaldo(e.saldo)).catch(() => {})
  }, [token, id, versao])

  async function enviar(e) {
    e.preventDefault()
    setMsg(null)
    try {
      const body = { idAluno: Number(form.idAluno), valor: Number(form.valor), motivo: form.motivo }
      const t = await api(`/api/professores/${id}/enviar-moedas`, { method: 'POST', body, token })
      setMsg({ ok: `${t.valor} moedas enviadas para ${t.aluno}.` })
      setForm({ idAluno: '', valor: '', motivo: '' })
      setVersao(versao + 1)
    } catch (err) {
      setMsg({ erro: err.message })
    }
  }

  const campo = nome => e => setForm({ ...form, [nome]: e.target.value })

  return (
    <>
      <p className="saldo">Seu saldo: <strong>{saldo ?? '…'}</strong> moedas</p>
      <Abas abas={[['enviar', 'Enviar moedas'], ['extrato', 'Extrato']]} atual={aba} onTrocar={setAba} />

      {aba === 'enviar' && (
        <form className="card" onSubmit={enviar}>
          <label>
            Aluno
            <select value={form.idAluno} onChange={campo('idAluno')} required>
              <option value="">Selecione…</option>
              {alunos.map(a => <option key={a.id} value={a.id}>{a.nome} — {a.curso}</option>)}
            </select>
          </label>
          <label>Quantidade de moedas<input type="number" min="1" max={saldo ?? undefined} value={form.valor} onChange={campo('valor')} required /></label>
          <label>Motivo do reconhecimento<textarea value={form.motivo} onChange={campo('motivo')} required rows={3} /></label>
          {msg?.ok && <p className="ok">{msg.ok}</p>}
          {msg?.erro && <p className="erro">{msg.erro}</p>}
          <button type="submit">Enviar</button>
        </form>
      )}

      {aba === 'extrato' && (
        <Extrato caminho={`/api/professores/${id}/extrato`} token={token} tipoUsuario="PROFESSOR" versao={versao} onSaldo={setSaldo} />
      )}
    </>
  )
}
