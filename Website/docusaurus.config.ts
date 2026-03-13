import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
  title: 'Diogenes',
  tagline: 'An open-source, robust licensing ecosystem for software and plugins.',
  url: 'https://efelleto.github.io',
  baseUrl: '/Diogenes/',
  organizationName: 'efelleto',
  projectName: 'Diogenes',
  onBrokenLinks: 'throw',

  future: {
    v4: true,
  },

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          editUrl: 'https://github.com/efelleto/Diogenes/',
        },
        blog: {
          showReadingTime: true,
          feedOptions: {
            type: ['rss', 'atom'],
            xslt: true,
          },
          editUrl: 'https://github.com/efelleto/Diogenes/',
          onInlineTags: 'warn',
          onInlineAuthors: 'warn',
          onUntruncatedBlogPosts: 'warn',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themes: [
    [
      require.resolve("@easyops-cn/docusaurus-search-local"),
      {
        hashed: true,
        language: ["en", "pt"],
        indexDocs: true,
        indexBlog: true,
        indexPages: true,
        highlightSearchTermsOnTargetPage: true,
        searchBarShortcutKeymap: "mod+k",
      },
    ],
  ],

  themeConfig: {
    // BARRA DE ANÚNCIO ADICIONADA AQUI
    announcementBar: {
      id: 'support_palestine',
      content:
        '🇵🇸 &nbsp; <b>We stand with the people of Palestine.</b> We encourage compassion and hope for peace. &nbsp; 🇵🇸<br>Please support humanitarian efforts and rescue refugees through the <a target="_blank" rel="noopener noreferrer" href="https://donate.unrwa.org/int/en/general">UNRWA</a>.',
      backgroundColor: '#000000', // Fundo preto para dar destaque às cores da bandeira
      textColor: '#ffffff',
      isCloseable: true,
    },
    colorMode: {
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: 'Home',
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Documentation',
        },
        {to: '/blog', label: 'Dev Logs', position: 'left'},
        {
          href: 'https://github.com/efelleto/Diogenes',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Documentation',
          items: [
            {
              label: 'Tutorial',
              to: '/docs/intro',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Discord',
              href: 'https://discord.gg/gku8uveC',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'Dev Logs',
              to: '/blog',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/efelleto/Diogenes',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Diogenes. Built with Docusaurus.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;