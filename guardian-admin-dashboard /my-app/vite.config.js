import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "https://guardian-backend-x5v6.vercel.app",
        changeOrigin: true,
        secure: true,
      },
    },
  },
});
