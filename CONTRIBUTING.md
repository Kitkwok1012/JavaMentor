# Branch Strategy

```
main (production)
  ↑
develop (integration)
  ↑
feature/* (new features)
  ↑
bugfix/* (bug fixes)
```

## How to Contribute

### New Feature
```bash
git checkout develop
git checkout -b feature/your-feature-name
# develop your feature
git push -u origin feature/your-feature-name
# create Pull Request to develop
```

### Bug Fix
```bash
git checkout develop  
git checkout -b bugfix/issue-description
# fix the bug
git push -u origin bugfix/issue-description
# create Pull Request to develop
```

### Release to Production
```bash
# Merge develop to main
git checkout main
git merge develop
git push origin main
```

## Rules
- `main` branch: Production-ready code only, no direct commits
- All changes must go through Pull Request to `develop`
- Feature branches should be based on `develop`
- Bugfix branches should be based on `develop`
