# 🏫 CEMEI Cacilda — Sistema de Gestão e Controle de Autorização de Uso de Imagem

![Java Servlet](https://img.shields.io/badge/Java-Servlet-orange?logo=openjdk)
![Apache Tomcat](https://img.shields.io/badge/Server-Apache%20Tomcat%2011-blue?logo=apachetomcat)
![Supabase](https://img.shields.io/badge/Database-Supabase-emerald?logo=supabase)
![HTML5/CSS3/JS](https://img.shields.io/badge/Frontend-HTML5%2FCSS3%2FJS-red?logo=html5)
![UNIVESP](https://img.shields.io/badge/Projeto%20Integrador-UNIVESP%202026-black)

Sistema web desenvolvido para a gestão, controlo e auditoria do uso de imagem de alunos do **CEMEI Cacilda** em conformidade com as diretrizes da **Portaria SEMEB nº 41/2026** e a **LGPD**.

O projeto integra um backend em **Java Servlets**, encriptação de dados de autenticação com **SHA-256**, base de dados **Supabase** via API REST e uma interface responsiva em tempo real.

---

## 📋 Índice
- [Arquitetura do Sistema](#-arquitetura-do-sistema)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Estrutura da Base de Dados (Supabase)](#-estrutura-da-base-de-dados-supabase)
- [Como Executar o Projeto Localmente](#-como-executar-o-projeto-localmente)
- [Estrutura de Pastas do Projeto](#-estrutura-de-pastas-do-projeto)
- [Segurança \& LGPD](#-segurança--lgpd)
- [Contexto Académico](#-contexto-académico)

---

## 🏗️ Arquitetura do Sistema

O sistema adota uma arquitetura em camadas (MVC simplificado):
- **Front-end:** HTML5, CSS3 dinâmico e JavaScript assíncrono (`fetch` API).
- **Back-end (Autenticação Segura):** Java Servlet (`CadastroServlet.java`) a correr no container **Apache Tomcat**, responsável pela encriptação de palavras-passe em SHA-256 e comunicação segura com o Supabase.
- **Base de Dados \& API REST:** **Supabase (PostgreSQL)** para persistência dos dados de alunos, termos e gestores escolares.

---

## 🚀 Tecnologias Utilizadas

| Camada | Tecnologia | Função |
| :--- | :--- | :--- |
| **Front-end** | HTML5, CSS3, JavaScript (ES6+) | Interface do utilizador, abas interativas, validações e consumo de API REST |
| **Back-end** | Java 17+, Jakarta Servlet API | Processamento de registo de gestores e encriptação SHA-256 |
| **Servidor** | Apache Tomcat 11.0.x | Container de Servlets Java |
| **Base de Dados** | Supabase (PostgreSQL) | Armazenamento relacional e disponibilização de API REST assíncrona |
| **Segurança** | SHA-256 (`java.security.MessageDigest`) | Encriptação irreversível de palavras-passe |

---

## ✨ Funcionalidades Principais

1. **🔒 Registo \& Autenticação Segura de Gestores (`login.html`)**
   - Registo de novos gestores de forma encriptada via Servlet Java.
   - Sessão guardada localmente com identificação do perfil de acesso no cabeçalho.
   - Funcionalidade de término de sessão (*Logout*).

2. **📊 Painel de Indicadores em Tempo Real (`index.html`)**
   - Atualização dinâmica de autorizações válidas, revogadas e termos pendentes diretamente a partir do Supabase.
   - Monitorização de cobertura de termos por turma escolar.

3. **📷 Verificação Prevenção "Antes de Postar"**
   - Seleção rápida de crianças presentes nas fotografias/vídeos.
   - Filtro por turma (*Berçário I*, *Berçário II*, *Maternal I*, *Maternal II*).
   - Consulta em tempo real à base de dados que valida se a publicação está **Liberada** ou se requer **Conferência**.

4. **📋 Baixa de Termos por Código Impresso**
   - Reativação/Dar baixa em autorizações através da leitura do código único do termo (ex.: `DP26-0012`, `FJ26-0005`).

5. **🔔 Registo de Revogação de Imagem**
   - Cancelamento de permissões de imagem a qualquer momento a pedido dos encarregados de educação (via `PATCH` no Supabase).

6. **🖨️ Gestão \& Impressão de Termos em Lote**
   - Criação dinâmica de novos modelos de eventos (ex.: *Festa Junina*, *Dia dos Pais*, *Feira de Ciências*).
   - Pré-visualização instantânea do termo impresso com código de rastreio.
   - Atalho nativo para impressão (`window.print()`).

---

## 🗄️ Estrutura da Base de Dados (Supabase)

Execute o script SQL abaixo no **SQL Editor** do Supabase para criar as tabelas necessárias:

```sql
-- 1. Tabela de alunos e autorizações
CREATE TABLE IF NOT EXISTS criancas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    turma VARCHAR(50) NOT NULL,
    codigo_termo VARCHAR(20) NOT NULL UNIQUE,
    autorizado BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Tabela de gestores/utilizadores
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL, -- Hash SHA-256 de 64 carateres
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Desativar RLS para protótipo de consulta via chave anon REST API
ALTER TABLE criancas DISABLE ROW LEVEL SECURITY;
ALTER TABLE usuarios DISABLE ROW LEVEL SECURITY;

-- Inserção de dados iniciais de teste
INSERT INTO criancas (nome, turma, codigo_termo, autorizado) VALUES
('Elisa Campos', 'Berçário I', 'FJ26-0005', true),
('Helena Prado', 'Berçário II', 'FJ26-0007', true),
('Bernardo Siqueira', 'Maternal I', 'DP26-0012', false),
('Manuela Rezende', 'Maternal I', 'DP26-0016', true),
('Ravi Antunes', 'Maternal II', 'BOS26-0023', true),
('Alice Martins', 'Maternal II', 'FJ26-0024', false);
