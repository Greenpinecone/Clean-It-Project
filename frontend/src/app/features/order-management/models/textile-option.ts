import { TextileType } from './textile-type';

export interface TextileOption {
  type: TextileType;
  label: string;
  description: string;
}

export const TEXTILE_OPTIONS: readonly TextileOption[] = [
  {
    type: TextileType.Shirt,
    label: 'Shirt',
    description: 'Business and casual shirts',
  },
  {
    type: TextileType.Suit,
    label: 'Suit',
    description: 'Two-piece suits and jackets',
  },
  {
    type: TextileType.Dress,
    label: 'Dress',
    description: 'Formal and everyday dresses',
  },
  {
    type: TextileType.Trousers,
    label: 'Trousers',
    description: 'Suit and casual trousers',
  },
  {
    type: TextileType.Coat,
    label: 'Coat',
    description: 'Coats and heavier outerwear',
  },
  {
    type: TextileType.Blouse,
    label: 'Blouse',
    description: 'Blouses and delicate tops',
  },
];
