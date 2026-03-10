import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Robust licensing system',
    Svg: require('@site/static/img/undraw_safe.svg').default,
    description: (
      <>
      High-performance infrastructure made in Kotlin. Manage keys, products, 
      and customers in real-time with total data integrity.
      </>
    ),
  },
  {
    title: 'Self-Sufficient',
    Svg: require('@site/static/img/undraw_server.svg').default,
    description: (
      <>
      Fully self-hosted architecture with zero external dependencies. Complete control over your data and server.
      </>
    ),
  },
  {
    title: 'Easy to Use',
    Svg: require('@site/static/img/undraw_ideas.svg').default,
    description: (
      <>
      Integration made simple via <code>JitPack</code>. A straightforward SDK that lets you deploy professional security and validation in minutes.
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
