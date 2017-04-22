# oska

A template for test-driven ClojureScript projects, using the Boot build tool.

# To use this template

```
git clone --depth=1 --single-branch https://github.com/dirv/oska <your-project-name>
cd !$
rm -rf .git
chmod a+x prepare.sh
./prepare.sh
```

### Running tests

`boot testing test-cljs`

or

`boot watch testing test-cljs`
