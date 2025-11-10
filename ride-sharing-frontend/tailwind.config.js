module.exports = {
  darkMode: '.dark-theme', // let tailwind use my own dark-theme class
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#13a4ec",
        "background-light": "#f6f7f8",
        "background-dark": "#101c22",
      },
      fontFamily: {
        display: ["Plus Jakarta Sans", "sans-serif"],
      },
    },
  },
  plugins: [
  ],
}
