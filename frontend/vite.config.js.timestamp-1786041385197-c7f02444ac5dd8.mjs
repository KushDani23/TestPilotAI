// vite.config.js
import { defineConfig } from "file:///D:/Personal/SmartAPITester/frontend/node_modules/vite/dist/node/index.js";
import react from "file:///D:/Personal/SmartAPITester/frontend/node_modules/@vitejs/plugin-react/dist/index.js";
var vite_config_default = defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy API calls to the Spring Boot backend during development.
    // This means /api/... in the frontend will be forwarded to http://localhost:8080/api/...
    // This is cleaner than specifying the full backend URL in every fetch call.
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcuanMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJEOlxcXFxQZXJzb25hbFxcXFxTbWFydEFQSVRlc3RlclxcXFxmcm9udGVuZFwiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9maWxlbmFtZSA9IFwiRDpcXFxcUGVyc29uYWxcXFxcU21hcnRBUElUZXN0ZXJcXFxcZnJvbnRlbmRcXFxcdml0ZS5jb25maWcuanNcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfaW1wb3J0X21ldGFfdXJsID0gXCJmaWxlOi8vL0Q6L1BlcnNvbmFsL1NtYXJ0QVBJVGVzdGVyL2Zyb250ZW5kL3ZpdGUuY29uZmlnLmpzXCI7aW1wb3J0IHsgZGVmaW5lQ29uZmlnIH0gZnJvbSAndml0ZSdcbmltcG9ydCByZWFjdCBmcm9tICdAdml0ZWpzL3BsdWdpbi1yZWFjdCdcblxuLy8gVml0ZSBjb25maWcgZm9yIFRlc3RQaWxvdCBBSSBmcm9udGVuZFxuLy8gaHR0cHM6Ly92aXRlanMuZGV2L2NvbmZpZy9cbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZyh7XG4gIHBsdWdpbnM6IFtyZWFjdCgpXSxcbiAgc2VydmVyOiB7XG4gICAgcG9ydDogNTE3MyxcbiAgICAvLyBQcm94eSBBUEkgY2FsbHMgdG8gdGhlIFNwcmluZyBCb290IGJhY2tlbmQgZHVyaW5nIGRldmVsb3BtZW50LlxuICAgIC8vIFRoaXMgbWVhbnMgL2FwaS8uLi4gaW4gdGhlIGZyb250ZW5kIHdpbGwgYmUgZm9yd2FyZGVkIHRvIGh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hcGkvLi4uXG4gICAgLy8gVGhpcyBpcyBjbGVhbmVyIHRoYW4gc3BlY2lmeWluZyB0aGUgZnVsbCBiYWNrZW5kIFVSTCBpbiBldmVyeSBmZXRjaCBjYWxsLlxuICAgIHByb3h5OiB7XG4gICAgICAnL2FwaSc6IHtcbiAgICAgICAgdGFyZ2V0OiAnaHR0cDovL2xvY2FsaG9zdDo4MDgwJyxcbiAgICAgICAgY2hhbmdlT3JpZ2luOiB0cnVlXG4gICAgICB9XG4gICAgfVxuICB9XG59KVxuIl0sCiAgIm1hcHBpbmdzIjogIjtBQUFtUyxTQUFTLG9CQUFvQjtBQUNoVSxPQUFPLFdBQVc7QUFJbEIsSUFBTyxzQkFBUSxhQUFhO0FBQUEsRUFDMUIsU0FBUyxDQUFDLE1BQU0sQ0FBQztBQUFBLEVBQ2pCLFFBQVE7QUFBQSxJQUNOLE1BQU07QUFBQTtBQUFBO0FBQUE7QUFBQSxJQUlOLE9BQU87QUFBQSxNQUNMLFFBQVE7QUFBQSxRQUNOLFFBQVE7QUFBQSxRQUNSLGNBQWM7QUFBQSxNQUNoQjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQ0YsQ0FBQzsiLAogICJuYW1lcyI6IFtdCn0K
