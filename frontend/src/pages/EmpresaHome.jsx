import { useEffect, useState } from 'react'
import { api } from '../api'
import Abas from '../components/Abas'
import Extrato from '../components/Extrato'
import EmpresaForm from '../components/EmpresaForm'

const VAZIO = { nome: '', descricao: '', foto: '', custo: '' }
const FOTO_MAX = 500 * 1024

export default function EmpresaHome({ sessao, onSair }) {
  const [aba, setAba] = useState('vantagens')
  const [vantagens, setVantagens] = useState([])
  const [perfil, setPerfil] = useState(null)
  const [form, setForm] = useState(VAZIO)
  const [msg, setMsg] = useState(null)
  const [versao, setVersao] = useState(0)
  const { token, id } = sessao

  useEffect(() => {
    api(`/api/empresas/${id}/vantagens`, { token }).then(setVantagens).catch(e => setMsg({ erro: e.message }))
    api(`/api/empresas/${id}`, { token }).then(setPerfil).catch(() => {})
  }, [token, id, versao])

  function lerFoto(e) {
    const arquivo = e.target.files[0]
    if (!arquivo) return
    if (arquivo.size > FOTO_MAX) {
      setMsg({ erro: 'A foto deve ter no máximo 500 KB.' })
      e.target.value = ''
      return
    }
    const leitor = new FileReader()
    leitor.onload = () => setForm(f => ({ ...f, foto: leitor.result }))
    leitor.readAsDataURL(arquivo)
  }

  async function cadastrar(e) {
    e.preventDefault()
    setMsg(null)
    if (!form.foto) return setMsg({ erro: 'Selecione uma foto do produto.' })
    try {
      await api(`/api/empresas/${id}/vantagens`, { method: 'POST', body: { ...form, custo: Number(form.custo) }, token })
      setForm(VAZIO)
      e.target.reset()
      setMsg({ ok: 'Vantagem cadastrada.' })
      setVersao(versao + 1)
    } catch (err) {
      setMsg({ erro: err.message })
    }
  }

  async function remover(v) {
    if (!confirm(`Remover "${v.nome}"?`)) return
    try {
      await api(`/api/empresas/${id}/vantagens/${v.id}`, { method: 'DELETE', token })
      setVersao(versao + 1)
    } catch (err) {
      setMsg({ erro: err.message })
    }
  }

  async function excluirConta() {
    if (!confirm('Excluir a empresa definitivamente?')) return
    try {
      await api(`/api/empresas/${id}`, { method: 'DELETE', token })
      onSair()
    } catch (err) {
      alert(err.message)
    }
  }

  const campo = nome => e => setForm({ ...form, [nome]: e.target.value })

  return (
    <>
      <Abas abas={[['vantagens', 'Minhas vantagens'], ['resgates', 'Resgates'], ['perfil', 'Perfil']]} atual={aba} onTrocar={setAba} />

      {aba === 'vantagens' && (
        <>
          <form className="card" onSubmit={cadastrar}>
            <h3>Nova vantagem</h3>
            <label>Nome<input value={form.nome} onChange={campo('nome')} required /></label>
            <label>Descrição<textarea value={form.descricao} onChange={campo('descricao')} required rows={2} /></label>
            <div className="grade2">
              <label>Custo (moedas)<input type="number" min="1" value={form.custo} onChange={campo('custo')} required /></label>
              <label>Foto do produto<input type="file" accept="image/*" onChange={lerFoto} /></label>
            </div>
            {form.foto && <img className="previa" src={form.foto} alt="Prévia" />}
            {msg?.ok && <p className="ok">{msg.ok}</p>}
            {msg?.erro && <p className="erro">{msg.erro}</p>}
            <button type="submit">Cadastrar vantagem</button>
          </form>
          <div className="vitrine">
            {vantagens.map(v => (
              <article key={v.id} className="card vantagem">
                <img src={v.foto} alt={v.nome} />
                <h3>{v.nome}</h3>
                <p>{v.descricao}</p>
                <div className="linha">
                  <strong>{v.custo} moedas</strong>
                  <button className="perigo" onClick={() => remover(v)}>Remover</button>
                </div>
              </article>
            ))}
          </div>
        </>
      )}

      {aba === 'resgates' && <Extrato caminho={`/api/empresas/${id}/extrato`} token={token} tipoUsuario="EMPRESA" versao={versao} />}

      {aba === 'perfil' && perfil && (
        <div className="card">
          <EmpresaForm empresa={perfil} token={token} onSalvo={setPerfil} />
          <hr />
          <button className="perigo" onClick={excluirConta}>Excluir empresa</button>
        </div>
      )}
    </>
  )
}
