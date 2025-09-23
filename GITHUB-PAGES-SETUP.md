# 📖 Configuración de GitHub Pages

## 🚀 Configuración Automática

### 1. **Workflow de GitHub Actions**

Se ha creado un workflow automático en `.github/workflows/deploy-docs.yml` que:

- **Se ejecuta** cuando hay cambios en `docs-site/`
- **Construye** la documentación automáticamente
- **Despliega** a GitHub Pages
- **Verifica** que el despliegue funcione

### 2. **Configuración en GitHub**

Para activar GitHub Pages:

1. **Ir a Settings** del repositorio
2. **Scroll down** a "Pages"
3. **Source**: Seleccionar "GitHub Actions"
4. **Save** la configuración

### 3. **URL de la Documentación**

Una vez configurado, la documentación estará disponible en:

**🌐 https://fgeorgescu.github.io/tacticore-backend/**

## 🔧 Configuración Manual

### Si prefieres configurar manualmente:

1. **Ir a Settings** → **Pages**
2. **Source**: "Deploy from a branch"
3. **Branch**: `gh-pages` (se creará automáticamente)
4. **Folder**: `/ (root)`

## 📊 Características Implementadas

### ✅ **Funcionalidades**

- **Despliegue automático** con GitHub Actions
- **Página 404 personalizada** para errores
- **Base path configurado** para GitHub Pages
- **Verificación automática** del despliegue
- **Comando Makefile** para abrir GitHub Pages

### ✅ **Archivos Creados**

- `.github/workflows/deploy-docs.yml` - Workflow de despliegue
- `docs-site/_404.md` - Página de error 404
- Configuración actualizada en `index.html`
- Comando `make docs-github` en Makefile

## 🚀 Cómo Usar

### **Comandos Disponibles**

```bash
# Abrir GitHub Pages
make docs-github

# Servir documentación local
make docs-serve

# Ver ayuda completa
make help
```

### **Despliegue Automático**

El despliegue se ejecuta automáticamente cuando:

- **Push** a `main` o `develop`
- **Cambios** en archivos de `docs-site/`
- **Ejecución manual** desde GitHub Actions

## 🔍 Verificación

### **Verificar Despliegue**

1. **Ir a Actions** en GitHub
2. **Verificar** que el workflow "Deploy Documentation to GitHub Pages" se ejecute
3. **Esperar** a que termine (2-3 minutos)
4. **Visitar** https://fgeorgescu.github.io/tacticore-backend/

### **Logs de Despliegue**

- **Build**: Construcción de la documentación
- **Deploy**: Subida a GitHub Pages
- **Verify**: Verificación del despliegue

## 🐛 Troubleshooting

### **Problemas Comunes**

#### Error: "Page build failed"

```bash
# Verificar que docs-site existe
ls -la docs-site/

# Verificar que index.html existe
ls -la docs-site/index.html
```

#### Error: "404 Not Found"

```bash
# Verificar configuración de basePath
grep -A 5 "basePath" docs-site/index.html
```

#### Error: "Workflow failed"

```bash
# Verificar logs en GitHub Actions
# Ir a Actions → Deploy Documentation to GitHub Pages
```

## 📚 Recursos Adicionales

### **Documentación**

- [GitHub Pages Documentation](https://docs.github.com/en/pages)
- [GitHub Actions for Pages](https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site#publishing-with-a-custom-github-actions-workflow)
- [Docsify Documentation](https://docsify.js.org/)

### **Enlaces Útiles**

- [GitHub Pages](https://pages.github.com/)
- [GitHub Actions](https://github.com/features/actions)
- [Docsify](https://docsify.js.org/)

## 🎯 Próximos Pasos

1. **Configurar GitHub Pages** en Settings
2. **Hacer push** de los cambios
3. **Verificar** el despliegue automático
4. **Compartir** la URL con el equipo

---

**¡La documentación estará disponible públicamente en GitHub Pages!** 🎉
