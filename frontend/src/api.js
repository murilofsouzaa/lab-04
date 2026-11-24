const BASE = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '')
const CHAVE = 'moeda.sessao'

export function lerSessao() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE))
  } catch {
    return null
  }
}

export function salvarSessao(sessao) {
  try {
    if (sessao) localStorage.setItem(CHAVE, JSON.stringify(sessao))
    else localStorage.removeItem(CHAVE)
  } catch {
    // armazenamento indisponível: a sessão vale só enquanto a página estiver aberta
  }
}

export async function api(caminho, { method = 'GET', body, token } = {}) {
  const headers = { 'Content-Type': 'application/json' }
  if (token) headers.Authorization = `Bearer ${token}`
  const resp = await fetch(BASE + caminho, { method, headers, body: body ? JSON.stringify(body) : undefined })
  if (resp.status === 401 && token) window.dispatchEvent(new Event('sessao-expirada'))
  if (resp.status === 204) return null
  const dados = await resp.json().catch(() => null)
  if (!resp.ok) throw new Error(dados?.erro || `Erro ${resp.status}`)
  return dados
}
