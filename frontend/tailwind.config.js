/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    '../cormacarena-client/src/main/resources/templates/**/*.{html,js}',
    '../cormacarena-organization/src/main/resources/templates/**/*.{html,js}'
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#004884',
        'secondary': '#717b85',
        'tertiary': '#E6EFFD',

        'button-red': '#513ea4',
      },
    },
  },
  plugins: [],
}
