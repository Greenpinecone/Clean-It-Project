import { TextileType } from './textile-type';

export interface TextileOption {
  type: TextileType;
  label: string;
  description: string;
  icon: string;
}

export const TEXTILE_OPTIONS: readonly TextileOption[] = [
  {
    type: TextileType.Shirt,
    label: 'Shirt',
    description: 'Business and casual shirts',
    icon: '👔',
  },
  {
    type: TextileType.Suit,
    label: 'Suit',
    description: 'Two-piece suits and jackets',
    icon: '🤵',
  },
  {
    type: TextileType.Dress,
    label: 'Dress',
    description: 'Formal and everyday dresses',
    icon: '👗',
  },
  {
    type: TextileType.Trousers,
    label: 'Trousers',
    description: 'Suit and casual trousers',
    icon: '👖',
  },
  {
    type: TextileType.Coat,
    label: 'Coat',
    description: 'Coats and heavier outerwear',
    icon: '🧥',
  },
  {
    type: TextileType.Blouse,
    label: 'Blouse',
    description: 'Blouses and delicate tops',
    icon: '👚',
  },
];
