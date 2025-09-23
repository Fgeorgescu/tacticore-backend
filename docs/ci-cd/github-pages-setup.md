# Configuración de GitHub Pages

## 🚀 Habilitar GitHub Pages (Configuración por Defecto)

### Paso 1: Ir a la configuración del repositorio
1. Ve a tu repositorio en GitHub: `https://github.com/Fgeorgescu/tacticore-backend`
2. Haz clic en **Settings** (Configuración)
3. En el menú lateral, busca **Pages** (Páginas)

### Paso 2: Configurar GitHub Pages
1. En **Source** (Fuente), selecciona **Deploy from a branch**
2. En **Branch**, selecciona **main**
3. En **Folder**, selecciona **/docs**
4. Haz clic en **Save**

### Paso 3: Verificar la configuración
- **Source**: Deploy from a branch
- **Branch**: main
- **Folder**: /docs
- **Custom domain**: (opcional)
- **Enforce HTTPS**: (recomendado)

## 🌐 URLs de Acceso

- **GitHub Pages**: `https://fgeorgescu.github.io/tacticore-backend/`
- **Repositorio**: `https://github.com/Fgeorgescu/tacticore-backend`

## 🔍 Verificación

### Verificar que GitHub Pages está habilitado
```bash
# Verificar en la configuración del repositorio
# Settings > Pages > Source: Deploy from a branch
# Branch: main, Folder: /docs
```

### Verificar el despliegue
```bash
# Verificar que la URL responde
curl -f "https://fgeorgescu.github.io/tacticore-backend/"

# Verificar que el contenido se actualiza
# Los cambios en /docs se reflejan automáticamente
```

## 📁 Estructura de Documentación

```
docs/
├── index.html          # Página principal de Docsify
├── _navbar.md          # Navegación superior
├── _sidebar.md         # Navegación lateral
├── README.md           # Página de inicio
├── api/                # Documentación de API
├── infrastructure/     # Documentación de infraestructura
├── development/        # Documentación de desarrollo
├── ci-cd/             # Documentación de CI/CD
└── references/        # Referencias y enlaces
```

## 🚨 Solución de Problemas

### Error: "Page not found"
- **Causa**: GitHub Pages no está habilitado
- **Solución**: Habilitar GitHub Pages en Settings > Pages

### Error: "404 Not Found"
- **Causa**: El contenido no se ha desplegado aún
- **Solución**: Esperar unos minutos para que GitHub procese los cambios

### Error: "Content not updating"
- **Causa**: Los cambios no se han propagado
- **Solución**: Verificar que los cambios estén en el branch `main`

## 📚 Ventajas de esta Configuración

### ✅ Simplicidad
- No requiere workflows complejos
- Configuración automática de GitHub
- Despliegue automático en cada push

### ✅ Confiabilidad
- Menos puntos de falla
- Configuración estándar de GitHub
- Fácil de mantener

### ✅ Flexibilidad
- Fácil de modificar
- No depende de acciones externas
- Control total sobre el contenido

## 🔄 Flujo de Trabajo

1. **Desarrollo**: Trabajar en la documentación en `/docs`
2. **Commit**: Hacer commit de los cambios
3. **Push**: Hacer push al branch `main`
4. **Despliegue**: GitHub Pages se actualiza automáticamente
5. **Verificación**: Verificar en la URL de GitHub Pages

## 📝 Notas Importantes

- Los cambios se reflejan automáticamente en GitHub Pages
- No se requiere configuración adicional
- La documentación está disponible públicamente
- Se puede usar dominio personalizado si es necesario

## 🛠️ Comandos Útiles

```bash
# Servir documentación localmente
make docs-serve

# Abrir documentación en navegador
make docs-open

# Ver instrucciones de GitHub Pages
make docs-github
```
