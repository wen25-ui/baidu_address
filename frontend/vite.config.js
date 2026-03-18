import fs from 'node:fs'
import path from 'node:path'
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

function copyBMapForMiniProgram() {
  const sourceFile = path.resolve(__dirname, 'src/libs/bmap-wx.min.js')

  return {
    name: 'copy-bmap-for-miniprogram',
    apply: 'build',
    writeBundle(outputOptions) {
      const outDir = outputOptions.dir ? path.resolve(__dirname, outputOptions.dir) : null
      if (!outDir || !fs.existsSync(sourceFile)) return

      const targetPaths = [
        path.resolve(outDir, 'libs/bmap-wx.min.js'),
        path.resolve(outDir, 'utils/bmap-wx.min.js')
      ]

      targetPaths.forEach((targetFile) => {
        fs.mkdirSync(path.dirname(targetFile), { recursive: true })
        fs.copyFileSync(sourceFile, targetFile)
      })
    }
  }
}

export default defineConfig({
  plugins: [uni(), copyBMapForMiniProgram()]
})
