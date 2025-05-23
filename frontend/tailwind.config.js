/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    '../cormacarena-client/src/**/*.{html,js,ts,jsx,tsx,java}',
    '../cormacarena-organization/src/**/*.{html,js,ts,jsx,tsx,java}'
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#00A3B7',
        'secondary': '#1b6b74',
        'tertiary': '#DEF2F1',

        'button-red': '#513ea4',
      },
    },
  },
  plugins: [],
}
