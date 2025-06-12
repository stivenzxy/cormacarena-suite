/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    '../cormacarena-client/src/main/resources/templates/**/*.{html,js}',
    '../cormacarena-organization/src/main/resources/templates/**/*.{html,js}'
  ],
  safelist: [
    {
      pattern: /bg-(red|blue|green)-(500|600)/,
    },
    {
      pattern: /text-\[#.*]/,
    },
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#0d348d',
        'secondary': '#717b85',
        'tertiary': '#E6EFFD',
        'primary-hover': '#0941a8',
        'button-red': '#e53e3e',
      },
    },
  },
  plugins: [],
}
