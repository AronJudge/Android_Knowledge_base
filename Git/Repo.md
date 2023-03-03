# repo

repo是Google开发的用于管理Android版本库的一个工具，

repo是使用Python对git进行了一定的封装，并不是用于取代git，

它简化了对多个Git版本库的管理。用repo管理的版本库都需要使用git命令来进行操作。
因此，使用repo工具之前，请先确保已经安装git。

为什么要用repo？

项目模块化/组件化之后各模块也作为独立的 Git 仓库从主项目里剥离了出去，各模块各自管理自己的版本。

Android源码引用了很多开源项目，每一个子项目都是一个Git仓库，每个Git仓库都有很多分支版本，

为了方便统一管理各个子项目的Git仓库，需要一个上层工具批量进行处理，因此repo诞生。

repo也会建立一个Git仓库，用来记录当前Android版本下各个子项目的Git仓库分别处于哪一个分支，

这个仓库通常叫做：manifest仓库(清单库)。

repo init
repo init命令

命令格式：

repo init [options] [manifest url]

    1

例如：

repo init -u manifest_git_path -m manifest_file_name -b branch_name --repo-url=repo_url --no-repo-verify

    1

命令效果：
首先当前目录产生一个.repo目录
然后克隆一份repo的源代码到.repo/repo下，里面存放了其他repo子命令，即repo的主体部分。
接着从manifest_git_path仓库地址clone清单库到.repo/manifests和.repo/manifests.git目录。
同时.repo目录下还包括manifest仓库(清单库)内容

常用选项：

    -u：指定Manifest库的Git访问路径。唯一必不可少的选项
    -m：指定要使用的Manifest文件。不指定的话，默认为default.xml文件
    -b：指定要使用Manifest仓库中的某个特定分支。
    --repo-url：指定repo的远端repoGit库的访问路径。
    --no-repo-verify：指定不要验证repo源码。
    --mirror：创建远程存储库的副本，而不是客户端工作目录。
    该选项用于创建版本库镜像。使用该选项则在下一步repo sync同步时，
    本地按照源的版本库组织方式进行组织，否则会按照 manifest.xml 指定的方式重新组织并检出到本地


修改获取repo的源码路径

前面已经说了下载下来的repo只是一个引导脚本，当执行repo init的时候才会下载repo的主体部分，并存放在当前目录的.repo/repo目录下。

这里就会涉及到一个问题，repo的主体部分是从哪里下载的？其实查看repo的引导脚本(/usr/local/bin/repo)可以发现，repo主体部分默认从https://gerrit.googlesource.com/git-repo获取(即，执行repo init命令时，不设置--repo-url选项)，这个网站需要科学上网才可以访问。


.repo文件夹简介

执行repo init命令之后，会在当前目录创建一个.repo文件夹。下面看看该文件夹下面都有什么东西吧。

$ tree .repo -L 1
.repo
├── manifests
├── manifests.git
├── manifest.xml
└── repo

3 directories, 1 file

文件夹	描述
manifests	manifest仓库(清单库)内容，即repo init的-u选项对应的仓库
manifests.git	manifest仓库(清单库)的.git目录
manifest.xml	指明当前生效的Manifest文件，即repo init的-m选项对应的参数(没有该选项时默认为default.xml)
repo	repo 命令的主体，包含了最新的 repo 命令
