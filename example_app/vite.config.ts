import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import { resolve } from "path";

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        port: 8080,
        host: true,
    },
    build: {
        cssCodeSplit: false,
        target: "es2023",
        sourcemap: false,
    },
    resolve: {
        alias: {
            "@root": resolve(import.meta.dirname, "./src"),
        },
    },
})
