import { describe, it } from 'node:test';
import assert from 'node:assert/strict';
import {
  allocateUniqueSiblingName,
  formatNumberedFilenameVariant,
  hasSiblingNameConflict,
  splitDisplayFileName,
} from '../string.js';

describe('sibling filename naming', () => {
  it('splits stem and extension', () => {
    assert.deepEqual(splitDisplayFileName('report.txt'), { stem: 'report', extension: 'txt' });
  });

  it('allocates numbered variants', () => {
    assert.equal(
      allocateUniqueSiblingName('report.txt', ['report.txt', 'report (2).txt']),
      'report (1).txt',
    );
    assert.equal(
      formatNumberedFilenameVariant('report', 1, 'txt'),
      'report (1).txt',
    );
  });

  it('detects sibling conflicts with optional exclusion', () => {
    assert.equal(hasSiblingNameConflict('Draft', ['Reports', 'Archive'], 'Reports'), false);
    assert.equal(hasSiblingNameConflict('Archive', ['Reports', 'Archive'], 'Reports'), true);
  });
});
